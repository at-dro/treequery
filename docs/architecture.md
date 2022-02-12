# TreeQuery: Architecture

## Subject Trees

The interface `SubjectNode` contains the methods described in the specification.

The default implementation `BaseSubjectNode` implements the matching logic through the following `protected` methods,
so they can be overridden individually:
* `typeMatches(type)`
* `propertiesMatch(properties, references)`, which calls `propertyMatches(key, value, references)` for each passed query property.
* `propertyMatches(key, value, references)`, which calls `propertyMatches(key, value)`
  (i.e., reference matching needs to be implemented by subclasses).
* `propertyMatches(key, value)`, which matches by object equality with the corresponding property of the subject node.

## Query Trees

Single query nodes are represented by instances of the `SingleQueryNode` class, which contains all specified information as fields.

Container query nodes are represented by instances of the abstract base class `ContainerQueryNode`, which contains the list of children.
Different subclasses implement the logic for different matching modes:
* `ExactQueryNode` is used for the `exact` mode.
* `AllQueryNode` is used for the `ordered` and `unordered` modes, which is specified as a flag.
* `AnyQueryNode` is used for the `any` and `optional` modes, which is specified as a flag.

All types of query nodes store a flag indicating whether the node itself or any of its descendants has the `reference` property set.
This flag is initialized on construction.

## Matching Implementation

### Subject Tree Traversal

Traversal of subject trees starting at arbitrary nodes is simplified by wrapping each node as a `LinkedSubjectNode` instance.
This class is useful, because regular subject node instances do not store references to their parent,
and therefore do not allow upward movements.

Each instance stores the following information:
* A reference to the node.
* A reference to the parent wrapper.
* A list of references to the children wrappers.
* The path from the root to this node, in the form of a sequence of indizes for the children list on each level.
  This list is created by appending a node's index to the parent path on construction.

Linked subject nodes allow the following operations:
* `getFirstChild()`:
  Get the first child of this node, or null if this node has no children.
* `neighbors()`:
  Get a stream of ordered neighbors of this node, starting at this node, i.e., the parent's children excluding the ones before this node.
* `getDirectNeighbor()`:
  Get the next child of this node's parent, or null if this node is the last child.
* `getNeighborWithin(ancestor)`:
  Get the next child of this node's parent, if it is not the last child.
  Otherwise, move up one level towards the root and try again, until reaching the specified ancestor.

  This method effectively performs the next step in a pre-order traversal of the subtree rooted at `ancestor`
  after the subtree rooted at the current node was fully processed.
* `getDirectChildOf(parent)`:
  If the current node is a direct child of the specified parent, return it.
  Otherwise, move up the path to the specified parent and return the next neighbor of the direct child.
* `getNext(ancestor)`:
  Get the first child, if one exists, or get the next neighbor within the specified ancestor.

  This method effectively performs the next step in a pre-order traversal of the subtree rooted at `ancestor`
  after the current node has been processed.
* `within(ancestor)`:
  Get a stream of all remaining nodes in the subtree rooted at `ancestor`,
  starting at the current node and then repeatedly using `getNext()`.

  This method effectively returns the sequence of a pre-order traversal from `ancestor`, starting at the current node.

All step operations should be implemented efficiently:
* Constant time for direct children and neighbors (assuming constant time list-access)
* Linear in the path length between node and specified ancestor (i.e., the height of the tree in the worst case)

### Matching Process

The matching process is based on a divide-and-conquer idea:
1. Find a matching subject node for a single query node based on the type and properties (i.e., a candidate node).
2. Obtain matches for the children of the query node (wrapped in a container) within the subtree rooted at the candidate.

The main component of the matching process is a `MatchingState`, which contains the following information:
* The previously found named references to subject nodes
* The root node for the currently considered subtree (i.e., the candidate for the parent query node)
* The next candidate node to consider

Each query node provides a method `findMatches(MatchingState start)`, which returns a stream of states after successfully matching
the query node and all of its descendants.
An empty stream indicates that the query node could not find a match for the given start state.

#### SingleQueryNode

Single query nodes are matched in the following steps:
1. Obtain a stream of nodes to check using pre-order traversal from the start node
   (limited to direct children of the current root if using the `direct` matching).
2. Filter for candidates by calling the `matches` method of the subject node using the query type and properties.
3. Calling the `findMatches` method on the children container for each candidate with a new matching state:
   * The candidate element is set as root of the state.
   * The candidate element's first child is set as next node to consider
   * The candidate element is added to the named references, if the query node specified a reference
4. The resulting states of the children matching are obtained:
   * There is no resulting state if the children matching was unsuccessful.
   * If no named references occur in the query subtree, all resulting states are equivalent (i.e., evaluation can stop after one is found).
   * There can be multiple distinct resulting states if there are named references.
5. If at least one match was found, a new matching state using the successful candidate element's next neighbor
   and the obtained references is returned.

#### ExactQueryNode

Exact query nodes iterate through all child queries and use the results for the last child query as candidates.

If the child query is a SingleQueryNode or another ExactQueryNode, no element may be skipped,
which is enforced by additional filtering of the results.

#### AllQueryNode

Ordered and unordered query nodes iterate through all child queries.
In the ordered case, the results for the last child query are used as candidates.
In the unordered case, matching always starts at the original candidate.

#### AnyQueryNode

Any and optional query nodes iterate through all child queries and combine all results.
Matching of each child query uses both the original candidate and the obtained intermediate results.

In the case of optional query nodes, the start candidate is returned as result if no match was found.

## XML Conversion

### XmlNode Wrapper

The library provides a wrapper around Java XML API nodes.
This wrapper provides the following helpers:
* `getChild(name)`: Obtain an Optional containing a wrapper for first child element with a given tag name if it exists.
* `getChildren()`: Obtain a Stream of wrappers for all child elements.
* `getChildren(name)`: Obtain a Stream of wrappers for child elements with the given tag name.
* `getName()`: Get the tag name of this element.
* `getValue()`: Get an Optional containing the non-recursive text content of this element if it contains text.
  Surrounding whitespace is only preserved within CDATA sections.
* `getNonNullValue()`: Get the text content as string, or throw an Exception if the node contains no value.
* `getAttributes()`: Get a map of all attributes of this element.
* `getAttribute(attribute)`: Get an Optional containing the value of the specified attribute if it exists.
* `getNonNullAttribute(attribute)`: Get the value of the specified attribute, or throw an Exception if it does not exist.
* `getIntAttribute(attribute)`: Get the integer value of the specified attribute,
   or throw an Exception if it does not exist or is non-integer.

### XmlCreator Wrapper

The library provides a wrapper for creating and serializing Java XML API nodes.
A new document is created using the constructor with the root tag name.
The wrapper provides the following helpers:
* `appendChild(name)`: Append a child with the given tag name and return the created wrapper.
* `setAttribute(key, value)`: Set the attribute to the given value.
* `setText(text)`: Set the text content using a CDATA section.
* `write(out)`: Write the XML content to an OutputStream.

### XmlConverter

This is the abstract base class for converting between XML and tree representations.
It provides the following methods:
* Parsing XML from files, classpath resources, and arbitrary InputStreams.
* Exporting XML to an OutputStream.

Subclasses need to implement:
* A `parse(XmlNode)` method, which is passed the root element of the XML document and should return the tree's root node.
* A `createXml(TreeNode, XmlCreator)` method, which is passed the tree's root node and the nullable parent XML element.

Implementations are given for subject trees and query trees, which implement both methods recursively.
