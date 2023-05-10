package com.insta.project.question.controller;

import com.insta.project.answer.AnswerForm;
import com.insta.project.answer.AnswerService;
import com.insta.project.question.QuestionForm;
import com.insta.project.question.domain.Question;
import com.insta.project.question.service.QuestionService;
import com.insta.project.user.AuthService;
import com.insta.project.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

@Controller
@RequestMapping("/question")

public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;

    @Autowired
    private AuthService authService;

    @RequestMapping("/user")
    public String UserDetail(Model model) {
        return "profile";
    }

    @RequestMapping("list/detail/{id}")
    public String showDetail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm, @AuthenticationPrincipal UserDetails userDetails){
        User user = authService.FindByEmail(userDetails.getUsername());
        Question question = this.questionService.getQuestion(id);
        Collections.sort(question.getAnswerList(), (a, b) -> b.getId() - a.getId());
        Collections.sort(question.getAnswerCommentsList(), (a, b) -> b.getId() - a.getId());
        model.addAttribute("question", question);
        model.addAttribute("user", user);
        return "question_detail";
    }



    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm){
        return "question_form";
    }

/*    @PostMapping("/create")
    public String questionPageCreate(Question question){
        questionService.create(question);
        return "redirect:/question/list";
    }*/

    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult){
        Question question = this.questionService.getQuestion(id);
        if(bindingResult.hasErrors()){
            model.addAttribute("question", question);
            return "story";
        }
        this.answerService.create(question, answerForm.getContent());
        return "redirect:/question/list";
    }

    @RequestMapping("/like/{id}")
    @ResponseBody
    public Integer questionLike(@PathVariable("id") Integer id){
        return this.questionService.setLike(id);
    }
}
