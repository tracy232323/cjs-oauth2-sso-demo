package com.cjs.sso.filter;

import com.cjs.sso.config.SpringContextUtils;
import com.cjs.sso.domain.MyUser;
import com.cjs.sso.entity.SysPermission;
import com.cjs.sso.entity.SysUser;
import com.cjs.sso.qw.QWService;
import com.cjs.sso.service.PermissionService;
import com.cjs.sso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MyAuthFilter extends OncePerRequestFilter {
    @Autowired
    private UserService userService;
    @Autowired
    private QWService qwService;
    @Autowired
    private PermissionService permissionService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //从企微获取免登code
        String code = httpServletRequest.getParameter("code");
        if (StringUtils.isNotBlank(code)) {
            //调用api查询code对应的人员电话，企微人员和业务系统人员通过电话号码关联，也可以通过userid等其他方式
            String mobile = qwService.getuserinfo(code);
            //查询本地人员信息
            SysUser sysUser = userService.getByMobile(mobile);
            PasswordEncoder passwordEncoder = SpringContextUtils.getBean(PasswordEncoder.class);
            //查询用户权限
            List<SysPermission> permissionList = permissionService.findByUserId(sysUser.getId());
            List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(permissionList)) {
                for (SysPermission sysPermission : permissionList) {
                    authorityList.add(new SimpleGrantedAuthority(sysPermission.getCode()));
                }
            }
            //通过spring security用户名密码校验
            MyUser myUser = new MyUser(sysUser.getUsername(), passwordEncoder.encode(sysUser.getPassword()), authorityList);
            //生成token
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(myUser, null, null);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
