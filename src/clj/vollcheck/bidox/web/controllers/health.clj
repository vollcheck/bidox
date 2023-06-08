(ns vollcheck.bidox.web.controllers.health
  (:require
    [ring.util.http-response :refer [ok]])
  (:import
   [java.util Date]
   [java.lang.management ManagementFactory]))

(defn healthcheck!
  [_req]
  (ok {:time     (str (Date. (System/currentTimeMillis)))
       :up-since (str (Date. (.getStartTime (ManagementFactory/getRuntimeMXBean))))
       :app      {:status  "up"
                  :message ""}}))
