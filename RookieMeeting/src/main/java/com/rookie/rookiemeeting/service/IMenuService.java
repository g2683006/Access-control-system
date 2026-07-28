package com.rookie.rookiemeeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rookie.rookiemeeting.entity.Menu;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xx
 * @since 2022-08-22
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> getRoleMenu(String roleFlag);

    List<Menu> findMenus(String name);

}
