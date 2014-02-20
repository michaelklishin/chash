;; Copyright (c) 2012-2014 Michael S. Klishin, Alex Petrov, and the ClojureWerkz team
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns clojurewerkz.chash.proplists
  "A very minimalistic and intentionally incomplete implementation of
   Erlang proplists (ordered pairs that support random access)"
  (:refer-clojure :exclude [count nth get contains? keys vals assoc]))

(defn create
  "Creates a new property list (a vector of pairs)"
  [& xs]
  (if (even? (clojure.core/count xs))
    (vec (doall (partition 2 xs)))
    (throw (IllegalArgumentException. "proplists must be initialized "))))

(def ^{:doc "An alias for clojure.core/count, which works exactly the same for proplists"}
  count clojure.core/count)

(def ^{:doc "An alias for clojure.core/nth, which will return the nth pair as a vector"}
  nth clojure.core/nth)

(defn get
  "Returns the first value associated with the given key"
  ([plist key]
     (some (fn [[k v]]
             (when (= key k) v))
           plist))
  ([plist key not-found]
     (or (get plist key) not-found)))

(defn contains?
  "Returns true if the list contains at least one entry for the given key, false otherwise"
  [plist key]
  (not (nil? (some (fn [[k v]]
                     (= key k))
                   plist))))

(defn keys
  [plist]
  (set (sort (doall (map first plist)))))

(defn vals
  [plist]
  (set (sort (doall (map second plist)))))

(defn index-of
  [plist k]
  (count (take-while (fn [[k' v]]
                       (not (= k' k))) plist)))

(defn assoc
  [plist k v]
  (let [i              (index-of plist k)
        [before after] (split-at i plist)]
    (concat before [[k v]] (rest after))))
