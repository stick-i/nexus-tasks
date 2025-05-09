package com.example.todo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.todo.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}