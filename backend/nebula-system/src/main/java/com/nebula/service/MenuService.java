package com.nebula.service;

import com.nebula.system.vo.req.MenuReq;
import com.nebula.system.vo.req.MenuTreeReq;
import com.nebula.system.vo.resp.MenuResp;

import java.util.List;

public interface MenuService {
    MenuResp saveMenu(MenuReq menuReq);
    MenuResp getMenuById(Long id);
    List<MenuResp> getAllMenus();
    List<MenuResp> getMenuTree();
    List<MenuResp> getMenuTree(MenuTreeReq req);
    List<MenuResp> getUserMenuTree(Long userId);
    MenuResp updateMenu(MenuReq menuReq);
    void deleteMenu(Long id);
}
