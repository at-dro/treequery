# TreeQuery: Specification

## Terminology

Requirement levels are used as specified in [RFC 2119](https://tools.ietf.org/html/rfc2119).

Additionally, a number of terms is used as follows in the context of this document:

* *Tree* refers to a *rooted ordered* tree.
  Note that the exact semantics of the order of children depends on the context and may be specified based on properties of tree nodes.
* *Nodes* have an arbitrary number of children unless restricted below.
  No distinction is made between the root, other inner nodes, or leaves.
  Leaves are simply considered to have an empty list of children.
  The parent of a node `x` is denoted as `p(x)`.
* *Subject* refers to a tree representing the data in which sub-structures are queried.
* *Query* refers to a tree specifying how the requested sub-structure must look.
* *Type* refers to a string value associated with a tree node.
* *Properties* are a (possibly empty) map of string-named objects associated with a tree node.

## Structure of the Subject Tree

Every subject node instance must support the following features:

* A subject node must have a type, which can be obtained with a public method.
* A subject node may have properties, which can be obtained with a public method.
* A subject node must have a method to obtain the (ordered) list of children.
* A subject node must have a method to check whether the subject node matches a supplied query type and query properties.
  A subject node should match if the query type and all individual query properties match the corresponding values of the subject node.
  A subject node's type should also be considered as matching if a null-value is supplied as query type.
  Custom implementations may choose different semantics for the query parameters.
* Furthermore, a map of named references to previously matched subject nodes may be given and considered for matching.
* Matching should not depend on the children in any way.

The library must provide an interface `SubjectNode`, which specifies the functionality listed above.
Furthermore, a base implementation `BaseSubjectNode` must be given.
Users of the library can use the provided implementation directly, override parts of it, or use a completely individual implementation.

## Structure of the Query Tree

The query tree consists of two different kinds of nodes:
A `SingleQueryNode` specifies the matching of a single subject node.
A `ContainerQueryNode` collects a group of query nodes to be matched.

A single query node contains the following information:

* A single query node may specify a type.
* A single query node may specify properties.
* A single query node may specify a reference name, which is used to store a back-reference to the subject node matching the query node.
* A single query node may specify one child query node, which is to be matched against the set of descendants of a matching subject node.
* A single query node `q` may specify that it only matches `s` if `p(q)` matched `p(s)`,
  i.e., the matching subject node must occur as a direct child of the matching ancestor (`direct` descendant matching).

A container query node contains the following information:

* A container query node must specify a matching mode.
  The following matching modes must be supported:
  * *Exact*: The container's children must match the candidate subject nodes in order, and no subject node may be skipped.
  * *Ordered*: The container's children must match the candidate subject nodes in order, but subject nodes may be skipped.
  * *Unordered*: Every child of the container must match a subject node, but subject nodes may be skipped or match multiple query nodes.
  * *Any*: At least of the container's children must match subject nodes (in any order).
    An empty list of children is considered a trivial match.
  * *Optional*: An arbitrary number of the container's children may match subject nodes (in any order).
    This is mainly useful for obtaining optional references.
* A container query node may have an arbitrary number of child query nodes.
* Containers may be nested, which allows for more complex matching of groups of children.

## XML Format

The library must provide implementations for converting between XML representations of subject and query trees and Java objects.

Note that the XML representation described below does not support every possible tree structure (e.g. because of limitations on tag names).

### Subject Tree XML

* The root element of the subject XML document must correspond to the root node of the subject tree.
* Children of a subject node are represented by child elements.
* The tag name of any element corresponds to the subject node type.
* Subject node properties are given as XML attributes, except the `value` property which can be given as CDATA text content.

### Query Tree XML

* The query tree XML document must contain a single root which can either correspond to a container query node or single query node.
* Container query nodes are represented with `container` elements.
  * The parsing implementation must support setting additional tag names for containers.
  * The query mode defaults to `ordered` and can be overridden by setting the `mode` attribute.
* All other elements are interpreted as single query nodes, where the tag name provides the type.
  * The `direct` attribute gives the corresponding flag, and the `reference` attribute gives the reference name.
  * The XML children are wrapped into a container query node, whose mode can again be overridden by setting the `mode` attribute.
  * All other attributes are added as query properties.
  * The text value of the XML element is added as `value` property.

## Test Cases

The test instances described below are formally specified as XML files in the directory [`src/test/resources/xml`](/src/test/resources/xml),
which is considered to be part of this specification.

### Real-World Matching Tests

These test cases are based on real-world AST (Abstract Syntax Tree) representations.
15 queries (`real/real01.xml` to `real/real15.xml`) are each applied to 3 subjects (`subject_real01.xml` to `subject_real03.xml`).

### Property Matching Tests

Different combinations of property matching are tested using the queries described below on the subject tree
[`subject_props.xml`](/src/test/resources/xml/subject/subject_props.xml).

1. Full subject tree with all properties queried
2. Full subject tree with a single property queried for each node
3. Partial structure with some properties queried
4. Single leaf node with all properties queried
5. Single inner node with all properties queried
6. Single node with incorrect property
7. Single node with non-existent property
8. Single node with non-existent combination of properties
9. Partial structure with non-existent property
10. Single node with all properties matching, but type incorrect

Queries 1–5 are positive test cases, queries 6–10 are negative test cases.

### Query Mode Tests

The different modes are tested comprehensively using the queries described below on the subject tree
[`subject_modes.xml`](/src/test/resources/xml/subject/subject_modes.xml).
Queries are adapted to the different modes by specifying that mode on key nodes.

1. Full subject tree
2. Part of the subject tree from root to leaves
3. Single leaf node
4. Two leaf nodes
5. Single inner node
6. Partial inner structure
7. Full subject tree with shuffled leaves
8. Partial inner structure with shuffled nodes
9. Two unordered leaf nodes
10. Duplicated node
11. Full tree with an additional non-existent leaf
12. Partial inner structure with a non-existent subtree
13. One existent, one none-existent leaf node
14. Two non-existent subtrees
15. Two non-existent leaf nodes
16. First level skipped
17. Intermediate level skipped

The following table states which queries are applied with which modes and the expected outcome (match found/match not found):

|   Query   |   exact   |  ordered  | unordered |    any    | optional  |  direct   |
|:---------:|:---------:|:---------:|:---------:|:---------:|:---------:|:---------:|
|     1     |     +     |     +     |     +     |     +     |     +     |     +     |
|     2     |     +     |     +     |     +     |     +     |     +     |     +     |
|     3     |     +     |     +     |     +     |     +     |     +     |     -     |
|     4     |     +     |     +     |     +     |     +     |     +     |     -     |
|     5     |     -     |     +     |     +     |     +     |     +     |     -     |
|     6     |     -     |     +     |     +     |     +     |     +     |     +     |
|     7     |     -     |     -     |     +     |     +     |     +     |           |
|     8     |     -     |     -     |     +     |     +     |     +     |           |
|     9     |     -     |     -     |     +     |     +     |     +     |           |
|    10     |     -     |     -     |     +     |     +     |     +     |           |
|    11     |     -     |     -     |     -     |     +     |     +     |           |
|    12     |     -     |     -     |     -     |     +     |     +     |           |
|    13     |     -     |     -     |     -     |     +     |     +     |           |
|    14     |     -     |     -     |     -     |     -     |     +     |           |
|    15     |     -     |     -     |     -     |     -     |     +     |           |
|    16     |           |           |           |           |           |     +     |
|    17     |           |           |           |           |           |     +     |

### Nested Container Matching Tests

These tests apply different queries containing nested containers to multiple subjects.

1. This query is applied to multiple subjects and uses a container within an `any` container.
2. This test case uses multiple variants of containers within an `exact` container.
   A total of 8 queries (a–d, each positive and negative) are applied to a single subject.

### Reference Matching Tests

This test loads all results different results and checks the returned references from
[`subject_props.xml`](/src/test/resources/xml/subject/subject_props.xml).

1. A single named query node may match multiple subject nodes.
2. Multiple named query nodes may match multiple subject nodes each, with one reference optional.

### XML Conversion Tests

The trees contained in multiple subject and query XML files are specified programmatically.
Each node of the parsing result is then compared with the specified tree.
Similarly, the specified tree is exported as XML and the result string compared to the expected XML.
