(ns clojurewerkz.chash.ring
  (:refer-clojure :exclude [get merge count contains?])
  (:require [clojurewerkz.support.hashing :as h]
            [clojurewerkz.support.core :as c]
            [clojurewerkz.chash.proplists :as pl]))

;;
;; Implementation
;;

(def ^{:const true :tag long}
  ring-top (dec (Math/pow 2 160)))

(defn ^long ring-increment
  [^long n]
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
  [^long n seed]
  (let [delta (ring-increment n)
        xs    (doall (range 0 ring-top delta))]
    (Ring. n (reduce (fn [plist p]
                        (conj plist [p seed]))
                      [] xs))))

(defn get
  [^Ring chash idx]
  (pl/get (.claims chash) idx))

(defn ^bytes key-of
  [value]
  (.asLong (h/sha1-of value)))

(defn claims
  [^Ring chash]
  (.claims chash))

(defn claimants
  [^Ring chash]
  (pl/vals (.claims chash)))

(defn partitions
  [^Ring chash]
  (pl/keys (.claims chash)))

(defn claimant?
  [^Ring chash node]
  (some #{node} (claimants chash)))

(defn count
  [^Ring chash]
  (.n-partitions chash))

(defn- random-of
  [& xs]
  (rand-nth xs))

(defn merge
  [^Ring one ^Ring another]
  (if (= (count one) (count another))
    (let [pairs  (partition 2 (interleave (.claims one) (.claims another)))
          merged (for [[a b] pairs]
                   (random-of a b))]
      (Ring. (count one) merged))
    (throw (IllegalArgumentException. "cannot merge two rings with different numbers of partitions"))))

(defn next-index
  [^Ring chash idx]
  (let [n (.n-partitions chash)
        i (ring-increment n)]
    (* i (rem (inc (quot idx i)) n))))

(defn predecessors
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
  [^Ring chash idx node]
  (Ring. (.n-partitions chash) (pl/assoc (.claims chash) idx node)))
