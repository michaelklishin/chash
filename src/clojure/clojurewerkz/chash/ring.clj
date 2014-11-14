;; Copyright (c) 2012-2014 Michael S. Klishin, Alex Petrov, and the ClojureWerkz team
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns clojurewerkz.chash.ring
  (:refer-clojure :exclude [get merge count contains? update])
  (:require [clojurewerkz.support.hashing :as h]
            [clojurewerkz.support.core :as c]
            [clojurewerkz.chash.proplists :as pl]))

;;
;; Implementation
;;

(def ^{:const true :tag 'long}
  ring-top (dec (Math/pow 2 160)))

(defn ring-increment
  ^double [^long n]
  (quot ring-top n))

(defrecord Ring [^long n-partitions ^clojure.lang.IPersistentList claims])

(defn max-n
  [^Ring chash ^long l]
  (min (.n-partitions chash) l))


;;
;; API
;;

(defn contains?
  [^Ring chash node]
  (pl/contains? (.claims chash) node))

(defn ^Ring fresh
  "Takes the size and seed node of a new ring, constructs and returns it"
  [^long n seed]
  (let [delta (ring-increment n)
        xs    (doall (range 0 ring-top delta))]
    (Ring. n (reduce (fn [plist p]
                        (conj plist [p seed]))
                      [] xs))))

(defn get
  "Returns the node that owns the partition identified by the provided index"
  [^Ring chash idx]
  (pl/get (.claims chash) idx))

(defn key-of
  "Returns value's key into the ring. Two values with the same SHA-1 hash value are
   considered the same name"
  ^long [value]
  (.asLong (h/sha1-of value)))

(defn claims
  [^Ring chash]
  (.claims chash))

(defn claimants
  "Returns all nodes that claim partitions on the ring"
  [^Ring chash]
  (pl/vals (.claims chash)))

(defn partitions
  "Returns all partitions in the ring"
  [^Ring chash]
  (pl/keys (.claims chash)))

(defn claimant?
  "Returns true if the given node claims a partition in the ring"
  [^Ring chash node]
  (some #{node} (claimants chash)))

(defn count
  "Returns sizes of the ring"
  [^Ring chash]
  (.n-partitions chash))

(defn- random-of
  [& xs]
  (rand-nth xs))

(defn merge
  "Randomized merging of two rings. When two nodes claim the same partition,
   the owner in the resulting ring is selected randomly"
  [^Ring one ^Ring another]
  (if (= (count one) (count another))
    (let [pairs  (partition 2 (interleave (.claims one) (.claims another)))
          merged (for [[a b] pairs]
                   (random-of a b))]
      (Ring. (count one) merged))
    (throw (IllegalArgumentException. "cannot merge two rings with different numbers of partitions"))))

(defn next-index
  "Returns the partition that follows the given key"
  [^Ring chash idx]
  (let [n (.n-partitions chash)
        i (ring-increment n)]
    (* i (rem (inc (quot idx i)) n))))

(defn predecessors
  "Returns all or up to n nodes in the ring before the given index as
   a property list (sequence of pairs)"  
  ([^Ring chash idx]
     (predecessors chash idx (.n-partitions chash)))
  ([^Ring chash idx n]
     (let [n' (max-n chash n)
           i  (ring-increment (.n-partitions chash))
           ;; split index
           si (inc (quot n i))
           [before after] (split-at si (.claims chash))
           ;; wrap around
           ordered        (reverse (concat after before))]
       (take n' ordered))))

(defn successors
  "Returns all or up to n nodes in the ring starting with the given index as
   a property list (sequence of pairs)"
  ([^Ring chash idx]
     (successors chash idx (.n-partitions chash)))
  ([^Ring chash idx n]
     (let [n' (max-n chash n)
           i  (ring-increment (.n-partitions chash))
           ;; split index
           si (inc (quot n i))
           [before after] (split-at si (.claims chash))
           ;; wrap around
           ordered        (concat after before)]
       (if (= (.n-partitions chash) n')
         ordered
         (take n' ordered)))))

(defn update
  "Updates claimant of the given partition"
  [^Ring chash idx node]
  (Ring. (.n-partitions chash) (pl/assoc (.claims chash) idx node)))
