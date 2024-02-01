package central.studio.provider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Index
 *
 * @author Alan Yeh
 * @since 2022/07/07
 */
@RestController
@RequestMapping("/provider")
public class ProviderIndexController {

    @GetMapping
    public String index() {
        return "Welcome to Central Provider.";
    }
}
