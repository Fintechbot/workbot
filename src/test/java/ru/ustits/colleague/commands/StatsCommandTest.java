package ru.ustits.colleague.commands;

import org.apache.commons.dbutils.QueryRunner;
import org.junit.Before;
import org.junit.Test;
import ru.ustits.colleague.repositories.MessageRepository;
import ru.ustits.colleague.repositories.services.MessageService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * @author ustits
 */
public class StatsCommandTest {

  private StatsCommand command;

  @Before
  public void setUp() throws Exception {
    command = new StatsCommand("random",
            new MessageService(mock(QueryRunner.class), mock(MessageRepository.class)));
  }

  @Test
  public void testBuildText() throws Exception {
    final Map<String, Integer> stats = new HashMap<>();
    final String firstUser = "Vanya";
    final Integer firstCount = 10;
    final String secondUser = "Petya";
    final Integer secondCount = 20;
    stats.put(firstUser, firstCount);
    stats.put(secondUser, secondCount);
    final String result = command.buildText(stats);
    assertThat(result)
            .contains(format("%s: %d", firstUser, firstCount))
            .contains(format("%s: %d", secondUser, secondCount));
  }

  @Test
  public void testBuildTextWithEmptyMap() throws Exception {
    final String result = command.buildText(Collections.emptyMap());
    assertThat(result).isEqualTo(StatsCommand.NO_STAT_MESSAGE);
  }

  @Test
  public void testBuildTextWithNull() throws Exception {
    assertThatThrownBy(() -> command.buildText(null))
            .isInstanceOf(IllegalArgumentException.class);
  }

}