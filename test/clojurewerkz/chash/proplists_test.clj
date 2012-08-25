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
  (let [p1 (pl/create :a 1 :b 2 :c 3 :d 4 :e 5)
        p2 (pl/create :a 1 :b 2 :c 30 :d 4 :e 5)]
    (is (= p2 (pl/assoc p1 :c 30)))))
