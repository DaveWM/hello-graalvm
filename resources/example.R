require(dplyr)
require(tibble)

function(value_in, date_in) {
    if (length(which(!is.na(value_in))) == 0)
    {'pass'}
    else {
        nrow <- tibble(value = value_in %>% replace(.,is.na(.),0),
                       date = date_in) %>%
            filter(is.na(date))
        
        ifelse(sum(nrow$value,na.rm=T)>=100,'fail','pass')
    } 
}
