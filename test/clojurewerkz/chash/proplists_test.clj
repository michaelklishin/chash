(ns clojurewerkz.chash.proplists-test
  (:require [clojurewerkz.chash.proplists :as pl])
  (:use clojure.test))


(deftest test-count
  (is (thrown? IllegalArgumentException
               (pl/create :a 1 :b)))
  (let [p (pl/create :a 1 :b 2)]
    (is (= 2 (pl/count p)))))

(deftest test-nth
  (let [p  (pl/create :a 1 :b 2 :c 3 :d 4 :e 5)
        i  (nth p 2)
        ;; alias
        i' (nth p 2)]
    (is (= i i' [:c 3]))))


(deftest test-get
  (testing "2-arity"
    (let [p (pl/create :a 1 :b 2 :c 3 :d 4 :e 5)]
      (are [k v] (is (= v (pl/get p k)))
           :a 1
           :b 2
           :c 3
           :d 4
           :e 5)))
  (testing "3-arity"
    (let [p (pl/create :a 1 :b 2 :c 3 :d 4 :e 5)]
      (are [k v] (is (= v (pl/get p k)))
           :a 1
           :b 2
           :c 3
           :d 4
           :e 5)
      (is (= :default (pl/get p :z :default))))))


(deftest test-contains?
  (let [p (pl/create :a 1 :b 2 :c 3 :d 4 :e 5)]
    (are [k] (is (pl/contains? p k))
         :a :b :c :d :e)
    (are [k] (is (not (pl/contains? p k)))
         :w :x :y :z :lol)))


(deftest test-keys
  (let [p (pl/create :a 1 :b 2 :c 3 :d 4 :e 5)]
    (is (= #{:a :b :c :d :e} (pl/keys p)))))

(deftest test-vals
  (let [p (pl/create :a 1 :b 2 :c 3 :d 4 :e 5)]
    (is (= #{1 2 3 4 5} (pl/vals p)))))

(deftest test-assoc
  (testing "case 1"
    (let [p1 (pl/create :a 1 :b 2 :c 3 :d 4 :e 5)
          p2 (pl/create :a 1 :b 2 :c 30 :d 4 :e 5)]
      (is (= p2 (pl/assoc p1 :c 30)))))
  (testing "case 2"
    (let [p1 (pl/create 0 "node@giove.local"
                        1.8268770466636286E47 "node@giove.local"
                        3.6537540933272573E47 "node@giove.local"
                        5.480631139990886E47 "node@giove.local"
                        7.3075081866545146E47 "node@giove.local"
                        9.134385233318143E47 "node@giove.local"
                        1.0961262279981772E48 "node@giove.local"
                        1.27881393266454E48 "node@giove.local")
          p2 (pl/create 0 "another@giove.local"
                        1.8268770466636286E47 "node@giove.local"
                        3.6537540933272573E47 "node@giove.local"
                        5.480631139990886E47 "node@giove.local"
                        7.3075081866545146E47 "node@giove.local"
                        9.134385233318143E47 "node@giove.local"
                        1.0961262279981772E48 "node@giove.local"
                        1.27881393266454E48 "node@giove.local")]
      (is (= p2 (pl/assoc p1 0 "another@giove.local"))))))

(deftest test-index-of
  (let [p (pl/create :a 1 :b 2 :c 3 :d 4)]
    (are [idx k] (is (= idx (pl/index-of p k)))
      0 :a
      1 :b
      2 :c
      3 :d)))

(deftest test-rand-nth
  (let [p (pl/create :a 1 :b 2 :c 3 :d 4 :e 5)
        n (rand-nth p)]
    (is n)))
