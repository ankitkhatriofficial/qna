package org.khatri.sto.qna.controller;

import jakarta.validation.Valid;
import org.khatri.sto.qna.constant.Constants;
import org.khatri.sto.qna.constant.ExceptionCode;
import org.khatri.sto.qna.dto.request.PostAnswerRequest;
import org.khatri.sto.qna.entity.UpVote;
import org.khatri.sto.qna.exceptions.InputInvalidException;
import org.khatri.sto.qna.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * @author Ankit Khatri
 */

@RestController
@RequestMapping("/ans")
public class AnswerController {

    @Autowired private AnswerService answerService;

    private void throwValidationErrorIfAny(final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = "Validation failed in fields: ".concat(bindingResult.getFieldErrors().stream().map(fe -> fe.getDefaultMessage()).collect(Collectors.joining(",")));
            throw new InputInvalidException(ExceptionCode.INVALID_REQUEST, errorMessage);
        }
    }

    @PostMapping("/write/{quesId}")
    public ResponseEntity<?> answerToQuestion(@RequestHeader(Constants.USER_ID_ATTR) Long userId, @PathVariable Long quesId,
                                              @Valid @RequestBody PostAnswerRequest request, BindingResult result){
        this.throwValidationErrorIfAny(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.answerService.writeAnswerToQuestion(userId, quesId, request));
    }

    @GetMapping("/get/{quesId}")
    public ResponseEntity<?> getAnswerOfQuestion(@PathVariable Long quesId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "5") int size,
                                                 @RequestParam(defaultValue = "createdAt") String sort,
                                                 @RequestParam(defaultValue = "desc") String direction){
        return ResponseEntity.ok(this.answerService.getAnswerOfQuestion(page, size, sort, direction, quesId));
    }

    @PutMapping("/modify/{answerId}")
    public ResponseEntity<?> modifyAnswer(@RequestHeader(Constants.USER_ID_ATTR) Long userId, @PathVariable Long answerId,
                                              @Valid @RequestBody PostAnswerRequest request, BindingResult result){
        this.throwValidationErrorIfAny(result);
        return ResponseEntity.ok(this.answerService.modifyAnswer(userId, answerId, request));
    }

    @PostMapping("/write/replies/{answerId}")
    public ResponseEntity<?> writeRepliesToAnswer(@RequestHeader(Constants.USER_ID_ATTR) Long userId, @PathVariable Long answerId,
                                                  @Valid @RequestBody PostAnswerRequest request, BindingResult result) {
        this.throwValidationErrorIfAny(result);
        return ResponseEntity.ok(this.answerService.writeRepliesToAnswer(userId, answerId, request));
    }

    @GetMapping("/get-replies/{answerId}")
    public ResponseEntity<?> getRepliesOfAnswer(@RequestHeader(Constants.USER_ID_ATTR) Long userId, @PathVariable Long answerId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size,
                                                @RequestParam(defaultValue = "createdAt") String sort,
                                                @RequestParam(defaultValue = "desc") String direction){
        return ResponseEntity.ok(this.answerService.getRepliesOfAnswer(page, size, sort, direction, userId, answerId));
    }

    @PostMapping("/suggest-vote/{answerId}")
    public ResponseEntity<?> suggestVote(@RequestHeader(Constants.USER_ID_ATTR) Long userId,
                                         @PathVariable Long answerId, @RequestParam("voteType") UpVote.UpVoteType voteType){
        this.answerService.suggestVoteOnAnswer(userId, answerId, voteType);
        return ResponseEntity.ok().build();
    }

}
