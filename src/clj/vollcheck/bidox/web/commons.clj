(ns vollcheck.bidox.web.commons
  (:require
   [integrant.repl.state :as state]
   ;; [xtdb.api :as xt]
   ))

(defn get-xt-node
    "Utility for getting current node of XTDB."
    []
    (get state/system :db.xtdb/node))
