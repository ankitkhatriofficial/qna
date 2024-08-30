package org.khatri.sto.qna.controller;

import jakarta.validation.Valid;
import org.khatri.sto.qna.constant.Constants;
import org.khatri.sto.qna.constant.ExceptionCode;
import org.khatri.sto.qna.dto.QuestionDto;
import org.khatri.sto.qna.entity.UpVote;
import org.khatri.sto.qna.exceptions.InputInvalidException;
import org.khatri.sto.qna.service.QuestionService;
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
@RequestMapping("/ques")
public class QuestionController {

    @Autowired private QuestionService questionService;

    private void throwValidationErrorIfAny(final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = "Validation failed in fields: ".concat(bindingResult.getFieldErrors().stream().map(fe -> fe.getDefaultMessage()).collect(Collectors.joining(",")));
            throw new InputInvalidException(ExceptionCode.INVALID_REQUEST, errorMessage);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewQuestion(@RequestHeader(Constants.USER_ID_ATTR) Long userId, @Valid @RequestBody QuestionDto questionDto, BindingResult result){
        this.throwValidationErrorIfAny(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.questionService.createNewQuestion(userId, questionDto));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllQuestionsOfUser(@RequestHeader(Constants.USER_ID_ATTR) Long userId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "5") int size,
                                                   @RequestParam(defaultValue = "createdAt") String sort,
                                                   @RequestParam(defaultValue = "desc") String direction){
        return ResponseEntity.ok(this.questionService.getAllUserQuestions(page, size, sort, direction, userId));
    }

    @GetMapping("/get/{quesId}")
    public ResponseEntity<?> getQuestionById(@RequestHeader(Constants.USER_ID_ATTR) Long userId, @PathVariable Long quesId){
        return ResponseEntity.ok(this.questionService.findQuestionById(userId, quesId));
    }

    @PutMapping("/modify/{quesId}")
    public ResponseEntity<?> updateExistingQuestion(@RequestHeader(Constants.USER_ID_ATTR) Long userId,
                                                    @PathVariable Long quesId,
                                                    @Valid @RequestBody QuestionDto questionDto, BindingResult result){
        this.throwValidationErrorIfAny(result);
        return ResponseEntity.ok(this.questionService.modifyExistingQuestion(userId, quesId, questionDto));
    }

    @DeleteMapping("/delete/{quesId}")
    public ResponseEntity<?> deleteQuestionId(@RequestHeader(Constants.USER_ID_ATTR) Long userId, @PathVariable Long quesId){
        this.questionService.deleteQuestion(userId, quesId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/extract-all")
    public ResponseEntity<?> getAllQuestions(@RequestHeader(value = Constants.USER_ID_ATTR, required = false) Long userId){
        return ResponseEntity.ok(this.questionService.findAllQuestions(userId));
    }

    @PostMapping("/suggest-vote/{quesId}")
    public ResponseEntity<?> suggestVote(@RequestHeader(Constants.USER_ID_ATTR) Long userId,
                                         @PathVariable Long quesId, @RequestParam("voteType") UpVote.UpVoteType voteType){
        this.questionService.suggestVoteOnQuestion(userId, quesId, voteType);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchQuestion(@RequestHeader(value = Constants.USER_ID_ATTR, required = false) Long userId,
                                            @RequestParam("query") String query,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size,
                                            @RequestParam(defaultValue = "createdAt") String sort,
                                            @RequestParam(defaultValue = "desc") String direction){
        return ResponseEntity.ok(this.questionService.searchQuestion(page, size, sort, direction, query));
    }

    @PostMapping("/search-by-tags")
    public ResponseEntity<?> searchQuestionByTags(@RequestHeader(value = Constants.USER_ID_ATTR, required = false) Long userId,
                                                  @RequestBody Object request){
        return ResponseEntity.ok(this.questionService.searchQuestionByTags(request));
    }
}
