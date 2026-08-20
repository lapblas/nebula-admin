package com.nebula.service.impl;

import com.nebula.entity.Menu;
import com.nebula.entity.User;
import com.nebula.exception.BusinessException;
import com.nebula.repository.MenuRepository;
import com.nebula.service.MenuService;
import com.nebula.service.UserService;
import com.nebula.system.vo.req.MenuReq;
import com.nebula.system.vo.req.MenuTreeReq;
import com.nebula.system.vo.resp.MenuResp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuResp saveMenu(MenuReq menuReq) {
        Menu menu = convertToMenu(menuReq);
        Menu savedMenu = menuRepository.save(menu);
        return convertToMenuResp(savedMenu);
    }

    @Override
    public MenuResp getMenuById(Long id) {
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        if (optionalMenu.isEmpty() || optionalMenu.get().getIsDeleted()) {
            throw new BusinessException(404, "菜单不存在");
        }
        return convertToMenuResp(optionalMenu.get());
    }

    @Override
    public List<MenuResp> getAllMenus() {
        List<Menu> menus = menuRepository.findAllByIsDeletedFalseOrderBySortOrder();
        return menus.stream()
                .map(this::convertToMenuResp)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuResp> getMenuTree() {
        return getMenuTree(new MenuTreeReq());
    }

    @Override
    public List<MenuResp> getMenuTree(MenuTreeReq req) {
        List<Menu> allMenus = menuRepository.findAllByIsDeletedFalseOrderBySortOrder();
        String menuName = req == null ? null : req.getMenuName();
        String path = req == null ? null : req.getPath();
        if (!StringUtils.hasText(menuName) && !StringUtils.hasText(path)) {
            return buildMenuTree(allMenus, 0L);
        }
        String name = menuName == null ? "" : menuName.trim().toLowerCase();
        String pathQuery = path == null ? "" : path.trim().toLowerCase();

        // 命中节点
        Set<Long> keepIds = allMenus.stream()
                .filter(menu -> matches(menu, name, pathQuery))
                .map(Menu::getId)
                .collect(Collectors.toSet());

        // 补齐所有祖先节点，保证树形结构完整
        boolean added = true;
        while (added) {
            added = false;
            for (Menu menu : allMenus) {
                if (keepIds.contains(menu.getId()) && menu.getParentId() != 0L && !keepIds.contains(menu.getParentId())) {
                    keepIds.add(menu.getParentId());
                    added = true;
                }
            }
        }

        List<Menu> kept = allMenus.stream()
                .filter(menu -> keepIds.contains(menu.getId()))
                .collect(Collectors.toList());
        return buildMenuTree(kept, 0L);
    }

    private boolean matches(Menu menu, String name, String pathQuery) {
        boolean nameOk = name.isEmpty() || (menu.getMenuName() != null && menu.getMenuName().toLowerCase().contains(name));
        boolean pathOk = pathQuery.isEmpty() || (menu.getPath() != null && menu.getPath().toLowerCase().contains(pathQuery));
        return nameOk && pathOk;
    }

    @Override
    public List<MenuResp> getUserMenuTree(Long userId) {
        User user = userService.getUserEntityById(userId);
        List<Menu> menus;

        // 管理员获取所有菜单
        if (Boolean.TRUE.equals(user.getIsAdmin())) {
            menus = menuRepository.findAllByIsDeletedFalseOrderBySortOrder();
        } else {
            // 获取用户权限标识
            Set<String> permissionKeys = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(permission -> permission.getPermissionKey())
                    .collect(Collectors.toSet());

            // 获取有权限的菜单
            menus = menuRepository.findAllByPermissionKeyInAndIsDeletedFalse(new ArrayList<>(permissionKeys));
        }

        return buildMenuTree(menus, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuResp updateMenu(MenuReq menuReq) {
        if (menuRepository.findById(menuReq.getId()).isEmpty()) {
            throw new BusinessException(404, "菜单不存在");
        }
        Menu menu = convertToMenu(menuReq);
        Menu updatedMenu = menuRepository.save(menu);
        return convertToMenuResp(updatedMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        if (optionalMenu.isEmpty() || optionalMenu.get().getIsDeleted()) {
            throw new BusinessException(404, "菜单不存在");
        }
        Menu menu = optionalMenu.get();
        menu.setIsDeleted(true);
        menuRepository.save(menu);
    }

    /**
     * 构建菜单树
     */
    private List<MenuResp> buildMenuTree(List<Menu> menus, Long parentId) {
        List<MenuResp> tree = new ArrayList<>();
        Map<Long, List<Menu>> menuMap = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));

        buildChildren(tree, menuMap, parentId);
        return tree;
    }

    private void buildChildren(List<MenuResp> tree, Map<Long, List<Menu>> menuMap, Long parentId) {
        List<Menu> children = menuMap.getOrDefault(parentId, Collections.emptyList());
        for (Menu menu : children) {
            MenuResp menuResp = convertToMenuResp(menu);
            tree.add(menuResp);
            // 递归构建子菜单
            List<MenuResp> subChildren = new ArrayList<>();
            buildChildren(subChildren, menuMap, menu.getId());
            menuResp.setChildren(subChildren);
        }
    }

    private Menu convertToMenu(MenuReq menuReq) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuReq, menu);
        return menu;
    }

    private MenuResp convertToMenuResp(Menu menu) {
        MenuResp menuResp = new MenuResp();
        BeanUtils.copyProperties(menu, menuResp);
        return menuResp;
    }
}
