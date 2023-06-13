package spd.trello.security.extrafilter;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class CustomFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private CustomFilter customFilter;

    public CustomFilterConfigurer(CustomFilter customFilter) {
        this.customFilter = customFilter;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        builder.addFilterAfter(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
