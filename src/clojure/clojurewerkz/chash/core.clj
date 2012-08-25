(ns clojurewerkz.chash.core
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

(defrecord CHash [^long n-partitions ^clojure.lang.IPersistentList claims])

(defn max-n
  [^CHash chash ^long l]
  (min (.n-partitions chash) l))


;;
;; API
;;

(defn contains?
  [chash node]
  )

(defn ^CHash fresh
  [^long n seed]
  (let [delta (ring-increment n)
        xs    (doall (range 0 ring-top delta))]
    (CHash. n (reduce (fn [plist p]
                        (conj plist [p seed]))
                      [] xs))))

(defn get
  [^CHash chash idx]
  )

(defn ^bytes key-of
  [value]
  (.asLong (h/sha1-of value)))

(defn claims
  [^CHash chash]
  (.claims chash))

(defn claimants
  [^CHash chash]
  (pl/vals (.claims chash)))

(defn partitions
  [^CHash chash]
  (pl/keys (.claims chash)))

(defn claimant?
  [^CHash chash node]
  (some #{node} (claimants chash)))

(defn count
  [^CHash chash]
  (.n-partitions chash))

(defn merge
  [^CHash one ^CHash another]
  (comment TODO))

(defn next-index
  [^CHash chash idx]
  (let [n (.n-partitions chash)
        i (ring-increment n)]
    (* i (rem (inc (quot idx i)) n))))

(defn predecessors
  ([^CHash chash idx]
     (predecessors chash idx (.n-partitions chash)))
  ([^CHash chash idx n]
     (let [n' (max-n chash n)
           i  (ring-increment (.n-partitions chash))
           ;; split index
           si (inc (quot n i))
           [before after] (split-at si (.claims chash))
           ;; wrap around
           ordered        (reverse (concat after before))]
       (take n' ordered))))

(defn successors
  ([^CHash chash idx]
     (successors chash idx (.n-partitions chash)))
  ([^CHash chash idx n]
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
  [^CHash chash idx node]
  (CHash. (.n-partitions chash) (pl/assoc (.claims chash) idx node)))
