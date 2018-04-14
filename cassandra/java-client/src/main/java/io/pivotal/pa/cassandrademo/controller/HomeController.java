package io.pivotal.pa.cassandrademo.controller;

import io.pivotal.pa.cassandrademo.repo.SpeciesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jaguilar on 4/9/18.
 */
@Controller
public class HomeController {
    @Autowired
    private SpeciesRepo repo;

    @RequestMapping(value = "/")
    public String index(Model model) {
        //model.addAttribute("allSpecies", repo.findAll());
        model.addAttribute("allSpecies", repo.getLimitedSpecies(50));
        return "index";
    }
}
