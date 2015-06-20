(ns tenor.entities
  (:use [overtone.live]))

(defprotocol Entity
  (repr [this])
  (generated [this]))

(defrecord Note [note-value pitch-letter octave]
  Entity
  (repr [this] (keyword (str pitch-letter octave))))

(defn make-note [notation]
  (let [notation-re #"^(1|2|4|8|16)([A-G])([#b]?)([1-9])$"
        components (re-matches notation-re notation)]
    (if components
      (->Note
        (Integer. (nth components 1))
        (str (nth components 2) (nth components 3))
        (Integer. (nth components 4)))
      (throw (Exception. "Invalid notation")))))

(defn time-signature [note-count & [sig]]
  (cond
    (> note-count 3)
    (let [beat (rand-nth [2 3 4])
          sig (conj (or sig '()) beat)]
      (time-signature (- note-count beat) sig))
    (and (<= note-count 3) (> note-count 0))
    (let [sig (conj (or sig '()) note-count)]
      (time-signature 0 sig))
    :else sig))

(defn segment-beat [note-count note-value
                    & {:keys [sparseness]
                       :or {sparseness 1}}]
  (let [sixteenth-count (* (/ 16 note-value) note-count)
        notes (range 1 (inc sixteenth-count))
        distribution (cons true (repeat sparseness false))]
    (filter
      (fn [x] (or (= 1 x) (rand-nth distribution)))
      notes)))

(defn segment-measure [measure
                       & {:keys [running-count result note-value sparseness]
                          :or {running-count 0 result '()
                               note-value 16 sparseness 1}}]
  (if (empty? measure)
    result
    (let [result (concat result (map #(+ running-count %)
                                     (segment-beat (first measure)
                                                   note-value
                                                   :sparseness sparseness)))
          running-count (+ running-count (first measure))]
      (segment-measure (rest measure)
                       :running-count running-count
                       :result result
                       :note-value note-value
                       :sparseness sparseness))))

(defrecord Beat [note-count note-value]
  Entity
  (generated [this] (segment-beat note-count note-value)))

(defn generate-random-scale []
  (let [scale-type (rand-nth (keys SCALE))
        ninth-octave-count 131
        note-name (find-note-name (rand-int ninth-octave-count))]
    (scale note-name scale-type)))

(defn map-measure [measure scale]
  (map #(hash-map :pos %, :note (rand-nth scale)) measure))

(defrecord Measure [note-count note-value]
  Entity
  (generated [this] (map-measure
                      (segment-measure (time-signature note-count))
                      (generate-random-scale))))

(defmacro construct-note [time player entity]
  `(list 'at ~time (list ~player ~entity)))

(defmacro construct-measure [measure-maps base-time player]
  `(map #(construct-note (* (:pos %) ~base-time)
                         ~player (:note %))
        ~measure-maps))
