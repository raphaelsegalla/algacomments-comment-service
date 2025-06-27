package com.algaworks.algacomments.comment.service.api.client.impl;

import com.algaworks.algacomments.comment.service.api.client.ModerationClient;
import com.algaworks.algacomments.comment.service.api.client.RestClientFactory;
import com.algaworks.algacomments.comment.service.api.model.ModerationInput;
import com.algaworks.algacomments.comment.service.api.model.ModerationOutput;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ModerationClientImpl implements ModerationClient {

    private final RestClient restClient;

    public ModerationClientImpl(RestClientFactory factory) {
        this.restClient = factory.moderateRestClient();
    }

    @Override
    public ModerationOutput validateComment(ModerationInput input) {
        return null;
    }

}
