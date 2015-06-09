package com.crossover.trialtest.app.rest.rest;

import com.crossover.trialtest.app.rest.rest.representation.ExamSessionRep;
import com.crossover.trialtest.app.rest.rest.representation.OptionRep;
import com.crossover.trialtest.app.rest.rest.representation.QuestionRep;
import com.crossover.common.app.rest.RestControllerBase;
import com.crossover.trialtest.domain.Samples;
import com.crossover.trialtest.domain.examsession.ExamSession;
import com.crossover.trialtest.domain.examsession.ExamSessionService;
import com.crossover.trialtest.domain.examsession.SessionQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@Transactional
@RestController @RequestMapping("/examsession")
public class ExamSessionController extends RestControllerBase<ExamSessionRep, ExamSession, ExamSessionService> {

    @Autowired
    ExamSessionController(ExamSessionService service) {
        super(service);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/start/{examId}")
    ResourceSupport start(@PathVariable int examId) {
        ExamSession newSession = entityService.startSession(Samples.participants.suyama().getId(), examId);
        ExamSessionRep dto = new ExamSessionRep(newSession);
        return toResource(dto);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/finish")
    ResponseEntity finish(@RequestBody ExamSessionRep dto) {
        ExamSession examSession = repToEntity(dto);
        entityService.finish(examSession);
        return new ResponseEntity(HttpStatus.OK);
    }

    protected ExamSession repToEntity(ExamSessionRep dto) {
        ExamSession examSession = entityService.getById(dto.getId()).get();
        applySentRep(examSession, dto);
        return examSession;
    }

    @Override
    protected ExamSessionRep entityToRep(ExamSession examSession) {
        return new ExamSessionRep(examSession);
    }

    private void applySentRep(ExamSession examSession, ExamSessionRep dto) {
        for (int i = 0; i < dto.questions.size(); i++) {
            QuestionRep qd = dto.questions.get(i);
            SessionQuestion question = examSession.getQuestion(i + 1);
            for (int j = 0; j < qd.options.size(); j++) {
                OptionRep od = qd.options.get(j);
                if (od.isChecked)
                    question.chooseAnswer(j + 1);
            }
        }
    }
}
