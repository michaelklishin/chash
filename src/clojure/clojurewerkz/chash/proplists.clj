(ns ^{:doc "A very minimalistic and intentionally incomplete implementation of
            Erlang proplists (ordered pairs that support random access)"}
  clojurewerkz.chash.proplists
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
