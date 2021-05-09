package com.dexlace.mongo.service;

import com.dexlace.mongo.MongoApplication;
import com.dexlace.mongo.entity.Comment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/5/6
 */
//SpringBoot的Junit集成测试
@RunWith(SpringRunner.class)
//SpringBoot的测试环境初始化，参数：启动类
@SpringBootTest(classes = MongoApplication.class)
public class CommentServiceTest {

    //注入Service
    @Autowired
    private CommentService commentService;


    /**
     * * 保存一个评论
     */
    @Test
    public void testSaveComment() {
        Comment comment = new Comment();
        comment.setArticleid("11111111");
        comment.setContent("测试添加的数据333333333333333");
        comment.setCreatedatetime(LocalDateTime.now());
        comment.setUserid("898989898");
        comment.setNickname("究极无敌深圳三和大神");
        comment.setState("1");
        comment.setLikenum(0);
        comment.setReplynum(0);
        commentService.saveComment(comment);
    }


    /*** 查询所有数据 */
    @Test
    public void testFindAll() {
        List<Comment> list = commentService.findCommentList();
        for (Comment comment : list) {
            System.out.println(comment);
        }

    }

    /**
     * 测试根据id查询
     */
    @Test
    public void testFindCommentById() {
        Comment comment = commentService.findCommentById("6094b26a612739427c557b16");
        System.out.println(comment);
    }

    /**
     * 测试根据父id查询子评论的分页列表
     */
    @Test
    public void testFindCommentListPageByParentid() {
        Page<Comment> pageResponse = commentService.findCommentListPageByParentid("3", 1, 2);
        System.out.println("----总记录数：" + pageResponse.getTotalElements());
        System.out.println("----当前页数据：" + pageResponse.getContent());
    }


    /*** 点赞数+1 */
    @Test
    public void testUpdateCommentLikenum() {
        //对3号文档的点赞数+1
        commentService.updateCommentLikenum("6094b26a612739427c557b16");
    }
}
