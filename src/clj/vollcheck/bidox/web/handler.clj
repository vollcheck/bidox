(ns vollcheck.bidox.web.handler
  (:require
    [vollcheck.bidox.web.middleware.core :as middleware]
    [integrant.core :as ig]
    [ring.util.http-response :as http-response]
    [ring.middleware.cors :refer [wrap-cors]]
    [reitit.ring :as ring]
    [reitit.swagger-ui :as swagger-ui]))

(defmethod ig/init-key :handler/ring
  [_ {:keys [router api-path] :as opts}]
  (ring/ring-handler
   router
   (ring/routes
    ;; Handle trailing slash in routes - add it + redirect to it
    ;; https://github.com/metosin/reitit/blob/master/doc/ring/slash_handler.md
    (ring/redirect-trailing-slash-handler)
    (ring/create-resource-handler {:path "/"})
    (when (some? api-path)
      (swagger-ui/create-swagger-ui-handler {:path api-path
                                             :url  (str api-path "/swagger.json")}))
    (ring/create-default-handler
     {:not-found
      (constantly (-> {:status 404, :body "Page not found"}
                      (http-response/content-type "text/html")))
      :method-not-allowed
      (constantly (-> {:status 405, :body "Not allowed"}
                      (http-response/content-type "text/html")))
      :not-acceptable
      (constantly (-> {:status 406, :body "Not acceptable"}
                      (http-response/content-type "text/html")))}))
   {:middleware [(middleware/wrap-base opts)
                 (middleware/wrap-db opts)
                 #(wrap-cors %
                             :access-control-allow-credentials "true"
                             :access-control-allow-origin [#".*"]
                             :access-control-allow-headers #{"accept" "accept-encoding"
                                                            "accept-language" "authorization"
                                                            "content-type" "origin"}
                             :access-control-allow-methods [:get :put :post :delete :options])
                 #_(middleware/wrap-cors :access-control-allow-origin "*"
                                         :access-control-allow-methods [:get :post :put :delete])
                 ]}))


(defmethod ig/init-key :router/routes
  [_ {:keys [routes]}]
  (apply conj [] routes))

(defmethod ig/init-key :router/core
  [_ {:keys [routes] :as opts}]
  (ring/router ["" opts routes]))
