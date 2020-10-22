package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import za.co.photo_sharing.app_ws.model.request.CommentRequestModel;
import za.co.photo_sharing.app_ws.model.response.CommentRest;
import za.co.photo_sharing.app_ws.services.CommentService;
import za.co.photo_sharing.app_ws.shared.dto.CommentDTO;

import java.time.LocalDateTime;

@RestController
@RequestMapping("comment") // http://localhost:8080/comment/web-apps-platform
public class CommentResource {

    private static Logger LOGGER = LoggerFactory.getLogger(CommentResource.class);
    public static Logger getLog() {
        return LOGGER;
    }

    @Autowired
    private CommentService commentService;
    private ModelMapper modelMapper = new ModelMapper();

    @ApiOperation(value = "The Add Comment Endpoint",
            notes = "${userResource.AddNewComment.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "/addComment/{username}/{articleId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommentRest addComment(@PathVariable("username") String username,
                                  @PathVariable(name = "articleId") Long articleId,
                                  @RequestBody CommentRequestModel comment){
        getLog().info("Comment added by {} time {} ", username, LocalDateTime.now());
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        CommentDTO addCommentDto = commentService.addComment(commentDTO, articleId, username);
        CommentRest commentRest = modelMapper.map(addCommentDto, CommentRest.class);
        getLog().info("Comment details {} ", commentRest);
        return commentRest;
    }

    @ApiOperation(value="Update Comment Endpoint",
            notes="${userResource.UpdateComment.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @PutMapping(path = "{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public CommentRest updateComment(@PathVariable("id") Long id,
                                     @RequestBody CommentRequestModel comment){
        getLog().info("Updating comment with id: {} ", id);
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        CommentDTO updateComment = commentService.updateComment(commentDTO, id);
        return modelMapper.map(updateComment, CommentRest.class);
    }
}
