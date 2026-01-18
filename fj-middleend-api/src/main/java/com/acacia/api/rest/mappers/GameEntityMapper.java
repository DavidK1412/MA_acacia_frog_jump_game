package com.acacia.api.rest.mappers;

import com.acacia.api.rest.dto.GameCreateRequest;
import com.acacia.api.rest.dto.GameCreateResponse;
import com.acacia.app.domain.entity.game.create.CreateInput;
import com.acacia.app.domain.entity.game.output.GameCreateOutput;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameEntityMapper {
    GameEntityMapper INSTANCE = Mappers.getMapper(GameEntityMapper.class);

    CreateInput toCreateInput(GameCreateRequest gameRequest);
    GameCreateResponse fromCreateOutput(GameCreateOutput gameCreateOutput);
}
