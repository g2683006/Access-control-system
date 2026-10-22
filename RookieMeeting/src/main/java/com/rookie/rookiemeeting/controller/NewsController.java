package com.rookie.rookiemeeting.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.rookiemeeting.common.lang.Result;
import com.rookie.rookiemeeting.entity.News;
import com.rookie.rookiemeeting.service.INewsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    INewsService newsService;

    /**
     * 获取全部部门、分页查询
     *
     * @return
     */
    @ApiOperation("分页查询所有部门接口")
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String title) {
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title", title);
        return Result.succ(newsService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 新增和编辑部门
     *
     * @param news
     * @return
     */
    @ApiOperation("新增和编辑部门接口")
    @PostMapping("/save")
    public Result save(@RequestBody News news) {
        if (StrUtil.isBlank(news.getAddtime())) {
            news.setAddtime(DateUtil.now());
        }
        return Result.succ(newsService.saveOrUpdate(news));
    }

    /**
     * 根据id删除单个部门
     *
     * @param id
     * @return
     */
    @ApiOperation("根据ID删除单个部门接口")
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable(name = "id") Integer id) {
        return Result.succ(newsService.removeById(id));
    }

    /**
     * 批量删除多个部门
     *
     * @param ids
     * @return
     */
    @ApiOperation("批量删除多个部门接口")
    @PostMapping("/delete/batch")
    public Result batch(@RequestBody List<Integer> ids) {
        return Result.succ(newsService.removeBatchByIds(ids));
    }

    /**
     * 查询所有部门
     *
     * @return
     */
    @ApiOperation("查询所有部门接口")
    @GetMapping("/all")
    public Result all() {
        return Result.succ(newsService.list());
    }

}

