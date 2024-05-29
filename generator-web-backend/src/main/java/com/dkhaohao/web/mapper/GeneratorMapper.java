package com.dkhaohao.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dkhaohao.web.model.entity.Generator;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 86135
 * @description 针对表【generator(代码生成器)】的数据库操作Mapper
 * @createDate 2024-05-25 11:33:47
 * @Entity com.dkhaohao.web.model.entity.Generator
 */
public interface GeneratorMapper extends BaseMapper<Generator> {
    @Select("select id, distPath from generator where isDelete = 1")
    List<Generator> listDeleteGenerator();

}




