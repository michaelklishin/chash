(ns clojurewerkz.chash.core-test
  (:require [clojurewerkz.chash.core :as ch])
  (:use clojure.test))


(deftest test-ring-initialization
  (testing "a new ring has a specified size"
    (let [n 8
          r (ch/fresh n "node1@giove.local")]
      (is (= n (ch/count r)))
      (is (= #{"node1@giove.local"} (ch/nodes r)))))
  (testing "a new ring has all partitions claimed by the seed"
    (let [n 8
          r (ch/fresh n "node1@giove.local")]
      (is (= n (:n-partitions r)))
      ;; this is not sorted but we can easily produce a sorted
      ;; sequence of pairs (a la Erlang proplists) from it for operations
      ;; where we need ordering
      (is (= {1.27881393266454E48 "node1@giove.local"
              1.0961262279981772E48 "node1@giove.local"
              9.134385233318143E47 "node1@giove.local"
              7.3075081866545146E47 "node1@giove.local"
              5.480631139990886E47 "node1@giove.local"
              3.6537540933272573E47 "node1@giove.local"
              1.8268770466636286E47 "node1@giove.local"
              0 "node1@giove.local"}
             (ch/claims r))))))
