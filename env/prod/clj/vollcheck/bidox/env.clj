(ns vollcheck.bidox.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[vollcheck.bidox starting]=-"))
   :start      (fn []
                 (log/info "\n-=[vollcheck.bidox started successfully]=-"))
   :stop       (fn []
                 (log/info "\n-=[vollcheck.bidox has shut down successfully]=-"))
   :middleware (fn [handler _] handler)
   :opts       {:profile :prod}})
