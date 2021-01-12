package com.cjs.proj.controller;

import com.cjs.proj.pojo.template;
import com.cjs.proj.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/template")
public class TemplateController {
    @Autowired
    private TemplateService templateService;

    @GetMapping("/findAll")
    public List<template> findAll(){
        return templateService.findAll();
    }

}
