package com.novv.dzdesk.res.dto;

import com.novv.dzdesk.res.model.CommentModel;
import com.novv.dzdesk.res.model.UinfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijianglong on 2017/9/5.
 */

public class CommentModelListDTO implements Mapper<List<CommentModel>> {

    private int version;
    private List<CommentModelListDTO.CommentDTO> listDTOs;

    public CommentModelListDTO(List<CommentModelListDTO.CommentDTO> listDTOs) {
        this.listDTOs = listDTOs;
    }

    public List<CommentModelListDTO.CommentDTO> getListDTOs() {
        return listDTOs;
    }

    public void setListDTOs(List<CommentDTO> listDTOs) {
        this.listDTOs = listDTOs;
    }

    @Override
    public List<CommentModel> transform() {
        List<CommentModel> beanList = new ArrayList<>();
        for (CommentDTO dto : listDTOs) {
            beanList.add(dto.transform());
        }
        return beanList;
    }

    public static class CommentDTO implements Mapper<CommentModel> {

        private String comment;
        private String atime;
        private UinfoBean uinfo;
        private int up;
        private String replyid;
        private String id;

        @Override
        public CommentModel transform() {
            CommentModel commentModel = new CommentModel();

            if (replyid != null) {
                //设置回复的内容id点赞时间等
                commentModel.setReplyContent(comment);
                commentModel.setReplyCreateTime(atime);
                commentModel.setReplyId(id);
                //设置回复人的id昵称头像
                if (uinfo != null) {
                    commentModel.setReplyName(uinfo.getNickname());
                    commentModel.setReplyUserId(uinfo.getId());
                    commentModel.setReplyPicture(uinfo.getImg());
                }

            } else {
                //设置评论的内容id点赞时间等
                commentModel.setCommentContent(comment);
                commentModel.setCommentTime(atime);
                commentModel.setCommentPraiseNum(up + "");
                commentModel.setCommentId(id);

                //设置评论人的id昵称头像
                if (uinfo != null) {
                    commentModel.setCommentName(uinfo.getNickname());
                    commentModel.setCommentUserId(uinfo.getId());
                    commentModel.setCommentPicture(uinfo.getImg());
                }
            }
            return commentModel;
        }
    }
}
