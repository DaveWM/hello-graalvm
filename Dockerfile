FROM clojure:lein-alpine as builder
COPY . /tmp/
WORKDIR /tmp
RUN lein uberjar

FROM oracle/graalvm-ce:1.0.0-rc15
RUN gu install R

WORKDIR /tmp

RUN yum update -y && yum install -y wget bzip2

RUN wget https://cran.r-project.org/src/contrib/assertthat_0.2.1.tar.gz \
https://cran.r-project.org/src/contrib/glue_1.3.1.tar.gz \
https://cran.r-project.org/src/contrib/magrittr_1.5.tar.gz \
https://cran.r-project.org/src/contrib/pkgconfig_2.0.2.tar.gz \
https://cran.r-project.org/src/contrib/R6_2.4.0.tar.gz \
https://cran.r-project.org/src/contrib/Rcpp_1.0.1.tar.gz \
https://cran.r-project.org/src/contrib/Archive/rlang/rlang_0.3.1.tar.gz \
https://cran.r-project.org/src/contrib/crayon_1.3.4.tar.gz \
https://cran.r-project.org/src/contrib/cli_1.1.0.tar.gz \
https://cran.r-project.org/src/contrib/fansi_0.4.0.tar.gz \
https://cran.r-project.org/src/contrib/utf8_1.1.4.tar.gz \
https://cran.r-project.org/src/contrib/pillar_1.3.1.tar.gz \
https://cran.r-project.org/src/contrib/tibble_2.1.1.tar.gz \
https://cran.r-project.org/src/contrib/purrr_0.3.2.tar.gz \
https://cran.r-project.org/src/contrib/Rcpp_1.0.1.tar.gz \
https://cran.r-project.org/src/contrib/tidyselect_0.2.5.tar.gz \
https://cran.r-project.org/src/contrib/BH_1.69.0-1.tar.gz \
https://cran.r-project.org/src/contrib/plogr_0.2.0.tar.gz \
https://cran.r-project.org/src/contrib/dplyr_0.8.0.1.tar.gz

RUN R CMD INSTALL assertthat_0.2.1.tar.gz
RUN R CMD INSTALL glue_1.3.1.tar.gz
RUN R CMD INSTALL magrittr_1.5.tar.gz
RUN R CMD INSTALL pkgconfig_2.0.2.tar.gz
RUN R CMD INSTALL R6_2.4.0.tar.gz
RUN R CMD INSTALL Rcpp_1.0.1.tar.gz
RUN R CMD INSTALL rlang_0.3.1.tar.gz
RUN R CMD INSTALL crayon_1.3.4.tar.gz
RUN R CMD INSTALL cli_1.1.0.tar.gz
RUN R CMD INSTALL fansi_0.4.0.tar.gz
RUN R CMD INSTALL utf8_1.1.4.tar.gz
RUN R CMD INSTALL pillar_1.3.1.tar.gz
RUN R CMD INSTALL tibble_2.1.1.tar.gz
RUN R CMD INSTALL purrr_0.3.2.tar.gz
RUN R CMD INSTALL Rcpp_1.0.1.tar.gz
RUN R CMD INSTALL tidyselect_0.2.5.tar.gz
RUN R CMD INSTALL BH_1.69.0-1.tar.gz
RUN R CMD INSTALL plogr_0.2.0.tar.gz
RUN R CMD INSTALL dplyr_0.8.0.1.tar.gz

Env JAR_DIR "/usr/share/java"

COPY --from=builder /tmp/target/uberjar/hello-graalvm-0.1.0-SNAPSHOT-standalone.jar ${JAR_DIR}/hello-graalvm-0.1.0-SNAPSHOT-standalone.jar
COPY deploy /usr/local/deploy
COPY resources ${JAR_DIR}/scripts

ENTRYPOINT ["/usr/local/deploy/bin/run.sh"]