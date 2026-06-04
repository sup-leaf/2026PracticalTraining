package com.bjtumarket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjtumarket.entity.CrawlHistory;
import com.bjtumarket.mapper.CrawlHistoryMapper;
import com.bjtumarket.service.CrawlHistoryService;
import org.springframework.stereotype.Service;

@Service
public class CrawlHistoryServiceImpl extends ServiceImpl<CrawlHistoryMapper, CrawlHistory> implements CrawlHistoryService {
}
