(ns clj-google-spreadsheet.core
  (:require [clj-http.client :as client]))

(defn get-all-spreadsheets [access-token]
  (->
    (client/get 
      (str "https://spreadsheets.google.com/feeds/spreadsheets/private/full"
           "?" "access_token=" access-token
           "&" "alt=json")
      {:as :json
       :throw-entire-message? true})
    :body))

(defn get-spreadsheet-link-by-name [all-spreadsheets name]
  (->>
    all-spreadsheets
    :feed :entry 
    (filter #(= (-> % :title :$t) name))
    first :link
    (filter #(= (-> % :rel) "http://schemas.google.com/spreadsheets/2006#worksheetsfeed"))
    first :href))

(defn get-spreadsheet [sheet-url access-token]
  (->
    (client/get 
      (str sheet-url
           "?" "access_token=" access-token
           "&" "alt=json")
      {:as :json})
    :body))

(defn get-listfeed-url [spreadsheet]
  (->>
    spreadsheet
    :feed :entry
    first :link
    (filter #(= (-> % :rel) "http://schemas.google.com/spreadsheets/2006#listfeed"))
    first :href))

(defn get-listfeed [worksheet-listfeed access-token]
  (->
    (client/get 
      (str worksheet-listfeed
           "?" "access_token=" access-token
           "&" "alt=json")
      {:as :json})
    :body :feed :entry))
