package com.bottle.muselink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bottle.muselink.domain.ChatMessageMemory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @description 针对表【chat_message_memory(AI会话消息表)】的数据库操作Mapper
*/
@Mapper
public interface ChatMessageMemoryMapper extends BaseMapper<ChatMessageMemory> {
    /**
     * 获取最大消息序号
     */
    @Select("SELECT MAX(message_order) FROM chat_message_memory WHERE conversation_id = #{conversationId} AND is_deleted = 0")
    Integer getMaxOrder(@Param("conversationId") String conversationId);
    /**
     * 逻辑删除会话消息
     */
    @Update("UPDATE chatmemory SET is_delete = 1, update_time = NOW() WHERE conversation_id = #{conversationId} AND is_delete = 0")
    int logicalDeleteByConversationId(@Param("conversationId") String conversationId);
}




