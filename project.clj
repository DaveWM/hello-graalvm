(defproject hello-graalvm "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.graalvm.sdk/graal-sdk "1.0.0-rc14"]
                 [org.graalvm.truffle/truffle-api "1.0.0-rc14"]]
  :main ^:skip-aot hello-graalvm.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
