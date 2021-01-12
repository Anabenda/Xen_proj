package com.cjs.proj.service.impl;

import com.cjs.proj.mapper.TemplateMapper;
import com.cjs.proj.pojo.template;
import com.cjs.proj.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    @Override
    public List<template> findAll() {
        return templateMapper.selectAll();
    }
}
