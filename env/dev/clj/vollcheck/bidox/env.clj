(ns vollcheck.bidox.env
  (:require
    [clojure.tools.logging :as log]
    [vollcheck.bidox.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[vollcheck.bidox starting using the development or test profile]=-"))
   :start      (fn []
                 (log/info "\n-=[vollcheck.bidox started successfully using the development or test profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[vollcheck.bidox has shut down successfully]=-"))
   :middleware wrap-dev
   :opts       {:profile       :dev
                :persist-data? true}})
