package io.pivotal.pa.cassandrademo.controller;

import io.pivotal.pa.cassandrademo.domain.SearchForm;
import io.pivotal.pa.cassandrademo.repo.SpeciesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jaguilar on 4/9/18.
 */
@Controller
public class MainController {
    @Autowired
    private SpeciesRepo repo;

    @RequestMapping(value = {"/","/cassandra"})
    public String index(Model model) {
        //model.addAttribute("allSpecies", repo.findAll());
        model.addAttribute("allSpecies", repo.getLimitedSpecies(100));
        return "index";
    }

    @RequestMapping(value = {"/search","/cassandra/search"})
    public String search(@ModelAttribute("searchCommon") SearchForm searchCommon,
                         @ModelAttribute("searchCounty") SearchForm searchCounty,
                         BindingResult bindingResult, Model model) {
        if ( bindingResult.hasErrors() ) {
            return "search";
        }
        if (searchCommon != null && searchCommon.getCommonName() != null) {
            model.addAttribute("speciesByCommonName", repo.findByCommon_name(searchCommon.getCommonName()));
        }
        if (searchCounty != null && searchCounty.getCounty() != null) {
            model.addAttribute("speciesByCounty", repo.findByCounty(searchCounty.getCounty()));
        }
        return "search";
    }

}
