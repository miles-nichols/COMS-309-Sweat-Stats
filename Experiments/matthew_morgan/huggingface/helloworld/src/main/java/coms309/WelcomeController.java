package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 309";
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309: " + name;
    }

    @GetMapping("/{name}/{password}")
    public String welcome(@PathVariable String name, @PathVariable String password) {
        if (password.equals("Password123")){
            return "Valid Password: Access Granted for " + name;
        }else{
            return "Your Password Stinks: Denied for " + name + "!!!";
        }
    }
}
