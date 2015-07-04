;; tenor/harmony.clj
;; Procedures to create harmony from a melody

(ns tenor.harmony
  (:use [overtone.live]))

;; --- Construct chords from notes and note positions --- ;;

(defn random-chords [position note]
  (if (weighted-coin 0.33)
    (let [selected-chord (chord (find-note-name note) (rand-nth (keys CHORD)))]
      (hash-map :pos position, :note (into [] selected-chord)))))

(defn major-chords [position note]
  (if (weighted-coin 0.33)
    (let [selected-chord (chord (find-note-name note) :major)]
      (hash-map :pos position, :note (into [] selected-chord)))))

(defn minor-chords [position note]
  (if (weighted-coin 0.33)
    (let [selected-chord (chord (find-note-name note) :minor)]
      (hash-map :pos position, :note (into [] selected-chord)))))

(defn random-chords-octave-down [position note]
  (if (weighted-coin 0.33)
    (let [selected-chord (chord (find-note-name (- note 12)) (rand-nth (keys CHORD)))]
      (hash-map :pos position, :note (into [] selected-chord)))))

(defn major-chords-octave-down [position note]
  (if (weighted-coin 0.33)
    (let [selected-chord (chord (find-note-name (- note 12)) :major)]
      (hash-map :pos position, :note (into [] selected-chord)))))

(defn minor-chords-octave-down [position note]
  (if (weighted-coin 0.33)
    (let [selected-chord (chord (find-note-name (- note 12)) :minor)]
      (hash-map :pos position, :note (into [] selected-chord)))))
