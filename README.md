# Consistent Hashing Clojure Library

CHash is a yet another consistent hashing library in Clojure, heavily inspired by the [implementation in Riak Core](https://github.com/basho/riak_core/blob/master/src/chash.erl).



## Project Maturity

CHash is not a young project and based on the consistent hashing implementation from Riak Core which is mature.



## Artifacts

CHash artifacts are [released to Clojars](https://clojars.org/clojurewerkz/chash). If you are using Maven, add the following repository
definition to your `pom.xml`:

``` xml
<repository>
  <id>clojars.org</id>
  <url>http://clojars.org/repo</url>
</repository>
```

### The Most Recent Release

With Leiningen:

    [clojurewerkz/chash "1.1.0"]


With Maven:

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>chash</artifactId>
      <version>1.1.0</version>
    </dependency>



## Documentation & Examples

CHash is a very small library so there are no documentation guides.

``` clojure
(require '[clojurewerkz.chash.ring :as ch])

;; create a new ring with 64 partitions and a seed node (value)
(ch/fresh 64 "seed")

;; update a partition
(let [r (ch/fresh 64 "seed")]
  (ch/update r 0 "node2"))

;; retrieve 3 partitions in order starting at the given point on the ring
(let [r (ch/fresh 64 "seed")]
  (ch/successors r (ch/key-of 0) 3))

;; check if a particular node claims any partitions in the ring
(let [r (ch/fresh 64 "seed")]
  (ch/claimant? r "node2"))

;; given a key as an integer, get the next ring partition
(let [r (ch/fresh 64 "seed")]
  (ch/next-index r (ch/key-of 128)))

;; randomized merging of two rings. When two nodes claim the same partition,
;; the owner in the resulting ring is selected randomly
(let [r1 (ch/fresh 64 "node1")
      r2 (ch/fresh 64 "node2")]
  (ch/merge r1 r2))
```

See documentation strings for `clojurewerkz.chash.core` functions and [our test suite]().



## Supported Clojure Versions

CHash requires Clojure 1.4 or later.


## Continuous Integration Status

[![Continuous Integration status](https://secure.travis-ci.org/michaelklishin/chash.png)](http://travis-ci.org/michaelklishin/chash)



## CHash Is a ClojureWerkz Project

CHash is part of the [group of Clojure libraries known as ClojureWerkz](http://clojurewerkz.org), together with
[Monger](http://clojuremongodb.info), [Welle](http://clojureriak.info), [Langohr](https://github.com/michaelklishin/langohr), [Elastisch](https://github.com/clojurewerkz/elastisch), [Neocons](https://github.com/michaelklishin/neocons) and several others.


## Development

CHash uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make sure you have it installed and then run tests against
supported Clojure versions using

    lein all test

Then create a branch and make your changes on it. Once you are done with your changes and all tests pass, submit a pull request
on Github.



## License

Copyright (C) 2012-2015 Michael S. Klishin, Alex Petrov, and the ClojureWerkz team.

Double licensed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same as Clojure) or the [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

