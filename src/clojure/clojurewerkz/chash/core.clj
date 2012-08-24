(ns clojurewerkz.chash.core
  (:refer-clojure :except [get merge count])
  (:require [clojurewerkz.support.hashing :as h]
            [clojurewerkz.support.core :as c]))

;;
;; Implementation
;;

(def ^{:const true :tag long}
  ring-top (dec (Math/pow 2 160)))

(defn ^long ring-increment
  [^long n]
  (quot ring-top n))




;;
;; API
;;

(defrecord CHash [^long n-partitions ^clojure.lang.IPersistentMap claims])

(defn contains?
  [chash node]
  )

(defn ^CHash fresh
  [^long n seed]
  (let [delta (ring-increment n)
        xs    (doall (range 0 ring-top delta))]
    (CHash. n (reduce (fn [m p]
                        (assoc m p seed))
                      (sorted-map) xs))))

(defn get
  [^CHash chash ^long idx]
  )

(defn ^bytes key-of
  [value]
  (.asBytes (h/sha1-of value)))

(defn claims
  [^CHash chash]
  (.claims chash))

(defn nodes
  [^CHash chash]
  (-> (.claims chash) vals sort set))

(defn ^long count
  [^CHash chash]
  (clojure.core/count (.claims chash)))

(defn merge
  [^CHash one ^CHash another]
  )

(defn next-index
  [^CHash chash ^String k]
  )

(defn ordered-from
  [^CHash chash ^long idx]
  )

(defn predecessors
  [^CHash chash ^long idx]
  )

(defn successors
  [^CHash chash ^long idx ^long n]
  )

(defn update
  []
  )
