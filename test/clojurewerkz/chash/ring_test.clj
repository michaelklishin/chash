(ns clojurewerkz.chash.ring-test
  (:require [clojurewerkz.chash.ring :as ch]
            [clojure.test :refer :all]))


(def eight-partitions [[0 "node1@giove.local"]
                       [1.8268770466636286E47 "node1@giove.local"]
                       [3.6537540933272573E47 "node1@giove.local"]
                       [5.480631139990886E47 "node1@giove.local"]
                       [7.3075081866545146E47 "node1@giove.local"]
                       [9.134385233318143E47 "node1@giove.local"]
                       [1.0961262279981772E48 "node1@giove.local"]
                       [1.27881393266454E48 "node1@giove.local"]])

(deftest test-ring-initialization
  (testing "a new ring has a specified size"
    (let [n 8
          r (ch/fresh n "node1@giove.local")]
      (is (= n (ch/count r)))
      (is (= #{"node1@giove.local"} (ch/claimants r)))))
  (testing "a new ring has all partitions claimed by the seed"
    (let [n 8
          r (ch/fresh n "node1@giove.local")]
      (is (= n (:n-partitions r)))
      ;; this is not sorted but we can easily produce a sorted
      ;; sequence of pairs (a la Erlang proplists) from it for operations
      ;; where we need ordering
      (is (= eight-partitions
             (ch/claims r))))))


(deftest test-claimant?
  (let [n    8
        seed "node1@giove.local"
        r    (ch/fresh n seed)]
    (is (ch/claimant? r seed))
    (is (not (ch/claimant? r "node2@other.local")))))


(deftest test-max-n
  (let [n    8
        seed "node1@giove.local"
        r    (ch/fresh n seed)]
    (is (= 3 (ch/max-n r 3)))
    (is (= 8 (ch/max-n r 11)))))


(deftest test-update
  (let [node1 "old@host"
        node2 "new@host"
        n     8
        r     (ch/fresh n node1)
        r'    (ch/update r 5.480631139990886E47 node2)]
    (is (= [[0 "old@host"]
            [1.8268770466636286E47 "old@host"]
            [3.6537540933272573E47 "old@host"]
            [5.480631139990886E47 "old@host"]
            [7.3075081866545146E47 "old@host"]
            [9.134385233318143E47 "old@host"]
            [1.0961262279981772E48 "old@host"]
            [1.27881393266454E48 "old@host"]]
           (ch/claims r)))
    (is (= [[0 "old@host"]
            [1.8268770466636286E47 "old@host"]
            [3.6537540933272573E47 "old@host"]
            [5.480631139990886E47 "new@host"]
            [7.3075081866545146E47 "old@host"]
            [9.134385233318143E47 "old@host"]
            [1.0961262279981772E48 "old@host"]
            [1.27881393266454E48 "old@host"]]
           (ch/claims r')))))


(deftest test-successors
  (testing "initial partition"
    (let [node "node1@giove.local"
          key  (ch/key-of 0)
          n    8
          r    (ch/fresh n node)
          xs   (ch/successors r key 3)]
      (is (=  3 (count xs)))
      (is (= [[1.8268770466636286E47 "node1@giove.local"]
              [3.6537540933272573E47 "node1@giove.local"]
              [5.480631139990886E47 "node1@giove.local"]]
             xs))))
  (testing "random partition"
    (let [node "node1@giove.local"
          key  (ch/key-of 888392888)
          n    8
          r    (ch/fresh n node)
          xs   (ch/successors r key 4)]
      (is (=  4 (count xs)))
      (is (= [[1.8268770466636286E47 "node1@giove.local"]
              [3.6537540933272573E47 "node1@giove.local"]
              [5.480631139990886E47 "node1@giove.local"]
              [7.3075081866545146E47 "node1@giove.local"]]
             xs)))))


(deftest test-predecessors
  (testing "initial partition"
    (let [node "node1@giove.local"
          key  (ch/key-of 0)
          n    8
          r    (ch/fresh n node)
          xs   (ch/predecessors r key 3)]
      (is (=  3 (count xs)))
      (is (= [[0 "node1@giove.local"]
              [1.27881393266454E48 "node1@giove.local"]
              [1.0961262279981772E48 "node1@giove.local"]]
             xs))))
  (testing "random partition"
    (let [node "node1@giove.local"
          key  (ch/key-of 888392888)
          n    8
          r    (ch/fresh n node)
          xs   (ch/predecessors r key 4)]
      (is (=  4 (count xs)))
      (is (= [[0 "node1@giove.local"]
              [1.27881393266454E48 "node1@giove.local"]
              [1.0961262279981772E48 "node1@giove.local"]
              [9.134385233318143E47 "node1@giove.local"]]
             xs)))))


(deftest test-inverse-predecessors
  (let [node "node1@giove.local"
        key  (ch/key-of 4)
        n    8
        r    (ch/fresh n node)
        xs   (ch/successors r key)
        ys   (ch/predecessors r key)]
    (is (= n (count xs) (count ys)))
    (is (= xs (reverse ys)))))


(deftest test-next-index
  (let [n    8
        seed "node1@giove.local"
        r    (ch/fresh n seed)]
    (is (ch/partitions r) #{1.8268770466636286E47})
    (is (= 1.8268770466636286E47 (ch/next-index r (ch/key-of 1))))))


(deftest test-get
  (let [n    8
        seed "node@giove.local"
        alt  "node2@giove.local"
        idx  1.8268770466636286E47
        r    (ch/fresh n seed)
        r'   (ch/update r idx alt)]
    (is (= seed (ch/get r idx)))
    (is (= alt (ch/get r' idx)))))


(deftest test-merge
  (let [n     8
        node1 "node1@giove.local"
        node2 "node2@giove.local"
        r1    (ch/fresh n node1)
        r2    (ch/update (ch/fresh n node2) 0 node1)
        r3    (ch/merge r1 r2)]
    (is (= node1 (ch/get r3 0)))))
