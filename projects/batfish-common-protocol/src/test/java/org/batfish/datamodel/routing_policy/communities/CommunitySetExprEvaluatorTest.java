package org.batfish.datamodel.routing_policy.communities;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.google.common.collect.ImmutableMap;
import javax.annotation.Nonnull;
import org.batfish.datamodel.bgp.community.StandardCommunity;
import org.junit.Test;

/** Test of {@link CommunitySetExprEvaluator}. */
public final class CommunitySetExprEvaluatorTest {

  private static final CommunitySetExprEvaluator EVALUATOR =
      new CommunitySetExprEvaluator(CommunityContext.builder().build());

  private static @Nonnull CommunitySet eval(CommunitySetExpr communitySetExpr) {
    return communitySetExpr.accept(EVALUATOR);
  }

  @Test
  public void testVisitInputCommunities() {
    CommunitySet cs = CommunitySet.of(StandardCommunity.of(1L));
    CommunityContext ctx = CommunityContext.builder().setInputCommunitySet(cs).build();

    assertThat(InputCommunities.instance().accept(new CommunitySetExprEvaluator(ctx)), equalTo(cs));
  }

  @Test
  public void testVisitCommunitySetDifference() {
    CommunitySet cs = CommunitySet.of(StandardCommunity.of(1L), StandardCommunity.of(2L));

    assertThat(
        eval(
            new CommunitySetDifference(
                new LiteralCommunitySet(cs), new CommunityIs(StandardCommunity.of(1L)))),
        equalTo(CommunitySet.of(StandardCommunity.of(1L))));
  }

  @Test
  public void testVisitCommunitySetExprReference() {
    CommunitySet cs = CommunitySet.of(StandardCommunity.of(1L));
    CommunityContext ctx =
        CommunityContext.builder()
            .setCommunitySetExprs(ImmutableMap.of("defined", new LiteralCommunitySet(cs)))
            .build();

    assertThat(
        new CommunitySetExprReference("defined").accept(new CommunitySetExprEvaluator(ctx)),
        equalTo(cs));
  }

  @Test
  public void testVisitCommunitySetReference() {
    CommunitySet cs = CommunitySet.of(StandardCommunity.of(1L));
    CommunityContext ctx =
        CommunityContext.builder().setCommunitySets(ImmutableMap.of("defined", cs)).build();

    assertThat(
        new CommunitySetReference("defined").accept(new CommunitySetExprEvaluator(ctx)),
        equalTo(cs));
  }

  @Test
  public void testVisitCommunitySetUnion() {
    CommunitySet cs1 = CommunitySet.of(StandardCommunity.of(1L));
    CommunitySet cs2 = CommunitySet.of(StandardCommunity.of(2L));

    assertThat(
        eval(new CommunitySetUnion(new LiteralCommunitySet(cs1), new LiteralCommunitySet(cs2))),
        equalTo(CommunitySet.of(StandardCommunity.of(1L), StandardCommunity.of(2L))));
  }

  @Test
  public void testVisitLiteralCommunitySet() {
    CommunitySet cs = CommunitySet.of(StandardCommunity.of(1L));

    assertThat(
        eval(new LiteralCommunitySet(cs)), equalTo(CommunitySet.of(StandardCommunity.of(1L))));
  }
}
