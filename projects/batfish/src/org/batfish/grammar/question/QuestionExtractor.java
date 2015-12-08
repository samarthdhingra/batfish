package org.batfish.grammar.question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.batfish.grammar.BatfishExtractor;
import org.batfish.grammar.ParseTreePrettyPrinter;
import org.batfish.grammar.question.QuestionParser.*;
import org.batfish.common.BatfishException;
import org.batfish.question.Expr;
import org.batfish.question.FailureQuestion;
import org.batfish.question.ForwardingAction;
import org.batfish.question.GeneratedRoutePrefixExpr;
import org.batfish.question.IngressPathQuestion;
import org.batfish.question.LocalPathQuestion;
import org.batfish.question.MultipathQuestion;
import org.batfish.question.ProtocolDependenciesQuestion;
import org.batfish.question.Question;
import org.batfish.question.QuestionParameters;
import org.batfish.question.ReachabilityQuestion;
import org.batfish.question.TracerouteQuestion;
import org.batfish.question.VerifyProgram;
import org.batfish.question.VerifyQuestion;
import org.batfish.question.bgp_neighbor_expr.BaseCaseBgpNeighborExpr;
import org.batfish.question.bgp_neighbor_expr.BgpNeighborExpr;
import org.batfish.question.bgp_neighbor_expr.VarBgpNeighborExpr;
import org.batfish.question.boolean_expr.AndExpr;
import org.batfish.question.boolean_expr.BaseCaseBooleanExpr;
import org.batfish.question.boolean_expr.BooleanExpr;
import org.batfish.question.boolean_expr.SetContainsExpr;
import org.batfish.question.boolean_expr.IfExpr;
import org.batfish.question.boolean_expr.NeqExpr;
import org.batfish.question.boolean_expr.NotExpr;
import org.batfish.question.boolean_expr.OrExpr;
import org.batfish.question.boolean_expr.VarBooleanExpr;
import org.batfish.question.boolean_expr.bgp_neighbor.HasGeneratedRouteBgpNeighborBooleanExpr;
import org.batfish.question.boolean_expr.iface.EnabledInterfaceBooleanExpr;
import org.batfish.question.boolean_expr.iface.HasIpInterfaceBooleanExpr;
import org.batfish.question.boolean_expr.iface.IsLoopbackInterfaceBooleanExpr;
import org.batfish.question.boolean_expr.iface.IsisL1ActiveInterfaceBooleanExpr;
import org.batfish.question.boolean_expr.iface.IsisL1PassiveInterfaceBooleanExpr;
import org.batfish.question.boolean_expr.iface.IsisL2ActiveInterfaceBooleanExpr;
import org.batfish.question.boolean_expr.iface.IsisL2PassiveInterfaceBooleanExpr;
import org.batfish.question.boolean_expr.iface.OspfActiveInterfaceBooleanExpr;
import org.batfish.question.boolean_expr.iface.OspfPassiveInterfaceBooleanExpr;
import org.batfish.question.boolean_expr.integer.IntEqExpr;
import org.batfish.question.boolean_expr.integer.IntGeExpr;
import org.batfish.question.boolean_expr.integer.IntGtExpr;
import org.batfish.question.boolean_expr.integer.IntLeExpr;
import org.batfish.question.boolean_expr.integer.IntLtExpr;
import org.batfish.question.boolean_expr.ipsec_vpn.CompatibleIkeProposalsIpsecVpnBooleanExpr;
import org.batfish.question.boolean_expr.ipsec_vpn.CompatibleIpsecProposalsIpsecVpnBooleanExpr;
import org.batfish.question.boolean_expr.ipsec_vpn.HasRemoteIpsecVpnIpsecVpnBooleanExpr;
import org.batfish.question.boolean_expr.ipsec_vpn.HasSingleRemoteIpsecVpnIpsecVpnBooleanExpr;
import org.batfish.question.boolean_expr.node.BgpConfiguredNodeBooleanExpr;
import org.batfish.question.boolean_expr.node.BgpHasGeneratedRouteNodeBooleanExpr;
import org.batfish.question.boolean_expr.node.HasGeneratedRouteNodeBooleanExpr;
import org.batfish.question.boolean_expr.node.IsisConfiguredNodeBooleanExpr;
import org.batfish.question.boolean_expr.node.OspfConfiguredNodeBooleanExpr;
import org.batfish.question.boolean_expr.node.StaticConfiguredNodeBooleanExpr;
import org.batfish.question.boolean_expr.policy_map_clause.PermitPolicyMapClauseBooleanExpr;
import org.batfish.question.boolean_expr.prefix_space.OverlapsPrefixSpaceBooleanExpr;
import org.batfish.question.boolean_expr.route_filter_line.PermitLineBooleanExpr;
import org.batfish.question.boolean_expr.static_route.HasNextHopInterfaceStaticRouteBooleanExpr;
import org.batfish.question.boolean_expr.static_route.HasNextHopIpStaticRouteBooleanExpr;
import org.batfish.question.boolean_expr.string.StringEqExpr;
import org.batfish.question.boolean_expr.string.StringGeExpr;
import org.batfish.question.boolean_expr.string.StringGtExpr;
import org.batfish.question.boolean_expr.string.StringLeExpr;
import org.batfish.question.boolean_expr.string.StringLtExpr;
import org.batfish.question.int_expr.DifferenceIntExpr;
import org.batfish.question.int_expr.IntExpr;
import org.batfish.question.int_expr.LiteralIntExpr;
import org.batfish.question.int_expr.ProductIntExpr;
import org.batfish.question.int_expr.QuotientIntExpr;
import org.batfish.question.int_expr.SetSizeIntExpr;
import org.batfish.question.int_expr.SumIntExpr;
import org.batfish.question.int_expr.VarIntExpr;
import org.batfish.question.int_expr.bgp_neighbor.LocalAsBgpNeighborIntExpr;
import org.batfish.question.int_expr.bgp_neighbor.RemoteAsBgpNeighborIntExpr;
import org.batfish.question.int_expr.static_route.AdministrativeCostStaticRouteIntExpr;
import org.batfish.question.interface_expr.BaseCaseInterfaceExpr;
import org.batfish.question.interface_expr.InterfaceExpr;
import org.batfish.question.interface_expr.VarInterfaceExpr;
import org.batfish.question.ip_expr.IpExpr;
import org.batfish.question.ip_expr.bgp_neighbor.LocalIpBgpNeighborIpExpr;
import org.batfish.question.ip_expr.bgp_neighbor.RemoteIpBgpNeighborIpExpr;
import org.batfish.question.ip_expr.iface.IpInterfaceIpExpr;
import org.batfish.question.ip_expr.static_route.NextHopIpStaticRouteIpExpr;
import org.batfish.question.ipsec_vpn_expr.BaseCaseIpsecVpnExpr;
import org.batfish.question.ipsec_vpn_expr.IpsecVpnExpr;
import org.batfish.question.ipsec_vpn_expr.ipsec_vpn.RemoteIpsecVpnIpsecVpnExpr;
import org.batfish.question.node_expr.BaseCaseNodeExpr;
import org.batfish.question.node_expr.NodeExpr;
import org.batfish.question.node_expr.VarNodeExpr;
import org.batfish.question.policy_map_clause_expr.BaseCasePolicyMapClauseExpr;
import org.batfish.question.policy_map_clause_expr.PolicyMapClauseExpr;
import org.batfish.question.prefix_expr.PrefixExpr;
import org.batfish.question.prefix_expr.iface.PrefixInterfacePrefixExpr;
import org.batfish.question.prefix_expr.static_route.PrefixStaticRoutePrefixExpr;
import org.batfish.question.prefix_space_expr.PrefixSpaceExpr;
import org.batfish.question.prefix_space_expr.node.BgpOriginationSpaceExplicitNodePrefixSpaceExpr;
import org.batfish.question.route_filter_expr.BaseCaseRouteFilterExpr;
import org.batfish.question.route_filter_expr.RouteFilterExpr;
import org.batfish.question.route_filter_line_expr.BaseCaseRouterFilterLineExpr;
import org.batfish.question.route_filter_line_expr.RouteFilterLineExpr;
import org.batfish.question.statement.Assertion;
import org.batfish.question.statement.ForEachBgpNeighborStatement;
import org.batfish.question.statement.ForEachClauseStatement;
import org.batfish.question.statement.ForEachGeneratedRouteStatement;
import org.batfish.question.statement.ForEachInterfaceStatement;
import org.batfish.question.statement.ForEachIpsecVpnStatement;
import org.batfish.question.statement.ForEachLineStatement;
import org.batfish.question.statement.ForEachMatchProtocolStatement;
import org.batfish.question.statement.ForEachMatchRouteFilterStatement;
import org.batfish.question.statement.ForEachNodeBgpGeneratedRouteStatement;
import org.batfish.question.statement.ForEachNodeStatement;
import org.batfish.question.statement.ForEachOspfOutboundPolicyStatement;
import org.batfish.question.statement.ForEachProtocolStatement;
import org.batfish.question.statement.ForEachRemoteIpsecVpnStatement;
import org.batfish.question.statement.ForEachRouteFilterInSetStatement;
import org.batfish.question.statement.ForEachRouteFilterStatement;
import org.batfish.question.statement.ForEachStaticRouteStatement;
import org.batfish.question.statement.IfStatement;
import org.batfish.question.statement.IntegerAssignment;
import org.batfish.question.statement.PrintfStatement;
import org.batfish.question.statement.SetAddStatement;
import org.batfish.question.statement.SetClearStatement;
import org.batfish.question.statement.Statement;
import org.batfish.question.statement.SetDeclarationStatement;
import org.batfish.question.statement.UnlessStatement;
import org.batfish.question.static_route_expr.BaseCaseStaticRouteExpr;
import org.batfish.question.static_route_expr.StaticRouteExpr;
import org.batfish.question.static_route_expr.VarStaticRouteExpr;
import org.batfish.question.string_expr.StringExpr;
import org.batfish.question.string_expr.StringLiteralStringExpr;
import org.batfish.question.string_expr.iface.InterfaceStringExpr;
import org.batfish.question.string_expr.ipsec_vpn.IkeGatewayNameIpsecVpnStringExpr;
import org.batfish.question.string_expr.ipsec_vpn.IkePolicyNameIpsecVpnStringExpr;
import org.batfish.question.string_expr.ipsec_vpn.IpsecPolicyNameIpsecVpnStringExpr;
import org.batfish.question.string_expr.ipsec_vpn.NameIpsecVpnStringExpr;
import org.batfish.question.string_expr.ipsec_vpn.OwnerNameIpsecVpnStringExpr;
import org.batfish.question.string_expr.ipsec_vpn.PreSharedKeyHashIpsecVpnStringExpr;
import org.batfish.question.string_expr.node.NameNodeStringExpr;
import org.batfish.question.string_expr.protocol.ProtocolStringExpr;
import org.batfish.question.string_expr.route_filter.RouteFilterStringExpr;
import org.batfish.question.string_expr.static_route.StaticRouteStringExpr;
import org.batfish.representation.Flow;
import org.batfish.representation.FlowBuilder;
import org.batfish.representation.Ip;
import org.batfish.representation.IpProtocol;
import org.batfish.representation.Prefix;
import org.batfish.util.SubRange;
import org.batfish.util.Util;

public class QuestionExtractor extends QuestionParserBaseListener implements
      BatfishExtractor {

   private static final String ERR_CONVERT_ACTION = "Cannot convert parse tree node to action";

   private static final String ERR_CONVERT_BGP_NEIGHBOR = "Cannot convert parse tree node to bgp_neighbor expression";

   private static final String ERR_CONVERT_BOOLEAN = "Cannot convert parse tree node to boolean expression";

   private static final String ERR_CONVERT_INT = "Cannot convert parse tree node to integer expression";

   private static final String ERR_CONVERT_INTERFACE = "Cannot convert parse tree node to interface expression";

   private static final String ERR_CONVERT_IP = "Cannot convert parse tree node to IP expression";

   private static final String ERR_CONVERT_POLICY_MAP_CLAUSE = "Cannot convert parse tree node to policy map clause expression";

   private static final String ERR_CONVERT_PREFIX = "Cannot convert parse tree node to prefix expression";

   private static final String ERR_CONVERT_PREFIX_SPACE = "Cannot convert parse tree node to prefix_space expression";

   private static final String ERR_CONVERT_PRINTABLE = "Cannot convert parse tree node to printable expression";

   private static final String ERR_CONVERT_ROUTE_FILTER = "Cannot convert parse tree node to route_filter expression";

   private static final String ERR_CONVERT_ROUTE_FILTER_LINE = "Cannot convert parse tree node to route_filter_line expression";

   private static final String ERR_CONVERT_STATEMENT = "Cannot convert parse tree node to statement";

   private static final String ERR_CONVERT_STATIC_ROUTE = "Cannot convert parse tree node to static_route expression";

   private static final String ERR_CONVERT_STRING = "Cannot convert parse tree node to string expression";;

   private FlowBuilder _currentFlowBuilder;

   private String _flowTag;

   private QuestionParameters _parameters;

   private QuestionCombinedParser _parser;

   private Question _question;

   private ReachabilityQuestion _reachabilityQuestion;

   private Set<Flow> _tracerouteFlows;

   private VerifyProgram _verifyProgram;

   public QuestionExtractor(QuestionCombinedParser parser, String flowTag,
         QuestionParameters parameters) {
      _parser = parser;
      _flowTag = flowTag;
      _parameters = parameters;
   }

   private BatfishException conversionError(String error, ParserRuleContext ctx) {
      String parseTree = ParseTreePrettyPrinter.print(ctx, _parser);
      return new BatfishException(error + ": \n" + parseTree);
   }

   @Override
   public void enterDefault_binding(Default_bindingContext ctx) {
      String var = ctx.VARIABLE().getText().substring(1);
      VariableType type;
      Object value;
      if (ctx.action() != null) {
         type = VariableType.ACTION;
         value = ForwardingAction.fromString(ctx.action().getText());
      }
      else if (ctx.boolean_literal() != null) {
         type = VariableType.BOOLEAN;
         value = Boolean.parseBoolean(ctx.getText());
      }
      else if (ctx.integer_literal() != null) {
         type = VariableType.INT;
         value = Long.parseLong(ctx.integer_literal().getText());
      }
      else if (ctx.IP_ADDRESS() != null) {
         type = VariableType.IP;
         value = new Ip(ctx.IP_ADDRESS().getText());
      }
      else if (ctx.ip_constraint_complex() != null) {
         type = VariableType.SET_PREFIX;
         value = toPrefixSet(ctx.ip_constraint_complex());
      }
      else if (ctx.IP_PREFIX() != null) {
         type = VariableType.PREFIX;
         value = new Prefix(ctx.IP_PREFIX().getText());
      }
      else if (ctx.range() != null) {
         type = VariableType.RANGE;
         value = toRange(ctx.range());
      }
      else if (ctx.REGEX() != null) {
         type = VariableType.REGEX;
         value = ctx.REGEX().getText();
      }
      else if (ctx.STRING_LITERAL() != null) {
         type = VariableType.STRING;
         value = ctx.STRING_LITERAL().getText();
      }
      else {
         throw new BatfishException("Invalid binding for variable: \"" + var
               + "\"");
      }
      if (_parameters.getTypeBindings().get(var) == null) {
         _parameters.getTypeBindings().put(var, type);
         _parameters.getStore().put(var, value);
      }
   }

   @Override
   public void enterExplicit_flow(Explicit_flowContext ctx) {
      _currentFlowBuilder = new FlowBuilder();
      _currentFlowBuilder.setTag(_flowTag);
   }

   @Override
   public void enterFailure_question(Failure_questionContext ctx) {
      FailureQuestion failureQuestion = new FailureQuestion();
      _question = failureQuestion;
   }

   @Override
   public void enterFlow_constraint_dst_ip(Flow_constraint_dst_ipContext ctx) {
      String valueText = ctx.dst_ip.getText();
      Ip dstIp;
      if (ctx.VARIABLE() != null) {
         String var = valueText.substring(1);
         dstIp = _parameters.getIp(var);
      }
      else {
         dstIp = new Ip(valueText);
      }
      _currentFlowBuilder.setDstIp(dstIp);
   }

   @Override
   public void enterFlow_constraint_dst_port(Flow_constraint_dst_portContext ctx) {
      String valueText = ctx.dst_port.getText();
      int dstPort;
      if (ctx.VARIABLE() != null) {
         String var = valueText.substring(1);
         dstPort = (int) _parameters.getInt(var);
      }
      else {
         dstPort = Integer.parseInt(valueText);
      }
      _currentFlowBuilder.setDstPort(dstPort);
   }

   @Override
   public void enterFlow_constraint_ingress_node(
         Flow_constraint_ingress_nodeContext ctx) {
      String valueText = ctx.ingress_node.getText();
      String ingressNode;
      if (ctx.VARIABLE() != null) {
         String var = valueText.substring(1);
         ingressNode = _parameters.getString(var);
      }
      else {
         ingressNode = valueText;
      }
      _currentFlowBuilder.setIngressNode(ingressNode);
   }

   @Override
   public void enterFlow_constraint_ip_protocol(
         Flow_constraint_ip_protocolContext ctx) {
      String valueText = ctx.ip_protocol.getText();
      int ipProtocolInt;
      if (ctx.VARIABLE() != null) {
         String var = valueText.substring(1);
         ipProtocolInt = (int) _parameters.getInt(var);
      }
      else {
         ipProtocolInt = Integer.parseInt(valueText);
      }
      IpProtocol ipProtocol = IpProtocol.fromNumber(ipProtocolInt);
      _currentFlowBuilder.setIpProtocol(ipProtocol);
   }

   @Override
   public void enterFlow_constraint_src_ip(Flow_constraint_src_ipContext ctx) {
      String valueText = ctx.src_ip.getText();
      Ip srcIp;
      if (ctx.VARIABLE() != null) {
         String var = valueText.substring(1);
         srcIp = _parameters.getIp(var);
      }
      else {
         srcIp = new Ip(valueText);
      }
      _currentFlowBuilder.setSrcIp(srcIp);
   }

   @Override
   public void enterFlow_constraint_src_port(Flow_constraint_src_portContext ctx) {
      String valueText = ctx.src_port.getText();
      int srcPort;
      if (ctx.VARIABLE() != null) {
         String var = valueText.substring(1);
         srcPort = (int) _parameters.getInt(var);
      }
      else {
         srcPort = Integer.parseInt(valueText);
      }
      _currentFlowBuilder.setSrcPort(srcPort);
   }

   @Override
   public void enterIngress_path_question(Ingress_path_questionContext ctx) {
      IngressPathQuestion question = new IngressPathQuestion();
      _question = question;
   }

   @Override
   public void enterLocal_path_question(Local_path_questionContext ctx) {
      LocalPathQuestion question = new LocalPathQuestion();
      _question = question;
   }

   @Override
   public void enterMultipath_question(Multipath_questionContext ctx) {
      MultipathQuestion multipathQuestion = new MultipathQuestion();
      _question = multipathQuestion;
   }

   @Override
   public void enterProtocol_dependencies_question(
         Protocol_dependencies_questionContext ctx) {
      _question = new ProtocolDependenciesQuestion();
   }

   @Override
   public void enterReachability_constraint_action(
         Reachability_constraint_actionContext ctx) {
      _reachabilityQuestion.getActions().clear();
      ForwardingAction action = toAction(ctx.action_constraint());
      _reachabilityQuestion.getActions().add(action);
   }

   @Override
   public void enterReachability_constraint_dst_ip(
         Reachability_constraint_dst_ipContext ctx) {
      Set<Prefix> prefixes = toPrefixSet(ctx.ip_constraint());
      _reachabilityQuestion.getDstPrefixes().addAll(prefixes);
   }

   @Override
   public void enterReachability_constraint_dst_port(
         Reachability_constraint_dst_portContext ctx) {
      Set<SubRange> range = toRange(ctx.range_constraint());
      _reachabilityQuestion.getDstPortRange().addAll(range);
   }

   @Override
   public void enterReachability_constraint_final_node(
         Reachability_constraint_final_nodeContext ctx) {
      String regex = toRegex(ctx.node_constraint());
      _reachabilityQuestion.setFinalNodeRegex(regex);
   }

   @Override
   public void enterReachability_constraint_ingress_node(
         Reachability_constraint_ingress_nodeContext ctx) {
      String regex = toRegex(ctx.node_constraint());
      _reachabilityQuestion.setIngressNodeRegex(regex);
   }

   @Override
   public void enterReachability_constraint_ip_protocol(
         Reachability_constraint_ip_protocolContext ctx) {
      Set<SubRange> range = toRange(ctx.range_constraint());
      _reachabilityQuestion.getIpProtocolRange().addAll(range);
   }

   @Override
   public void enterReachability_constraint_src_ip(
         Reachability_constraint_src_ipContext ctx) {
      Set<Prefix> prefixes = toPrefixSet(ctx.ip_constraint());
      _reachabilityQuestion.getSrcPrefixes().addAll(prefixes);
   }

   @Override
   public void enterReachability_constraint_src_port(
         Reachability_constraint_src_portContext ctx) {
      Set<SubRange> range = toRange(ctx.range_constraint());
      _reachabilityQuestion.getSrcPortRange().addAll(range);
   }

   @Override
   public void enterReachability_question(Reachability_questionContext ctx) {
      _reachabilityQuestion = new ReachabilityQuestion();
      _question = _reachabilityQuestion;
   }

   @Override
   public void enterTraceroute_question(Traceroute_questionContext ctx) {
      TracerouteQuestion question = new TracerouteQuestion();
      _question = question;
      _tracerouteFlows = question.getFlows();
   }

   @Override
   public void enterVerify_question(Verify_questionContext ctx) {
      VerifyQuestion verifyQuestion = new VerifyQuestion(_parameters);
      _question = verifyQuestion;
      _verifyProgram = verifyQuestion.getProgram();
      for (StatementContext sctx : ctx.statement()) {
         Statement statement = toStatement(sctx);
         _verifyProgram.getStatements().add(statement);
      }
   }

   @Override
   public void exitExplicit_flow(Explicit_flowContext ctx) {
      Flow flow = _currentFlowBuilder.build();
      _tracerouteFlows.add(flow);
   }

   public Question getQuestion() {
      return _question;
   }

   @Override
   public void processParseTree(ParserRuleContext tree) {
      ParseTreeWalker walker = new ParseTreeWalker();
      walker.walk(this, tree);
   }

   private ForwardingAction toAction(Action_constraintContext ctx) {
      ForwardingAction action;
      if (ctx.action() != null) {
         if (ctx.action().ACCEPT() != null) {
            action = ForwardingAction.ACCEPT;
         }
         else if (ctx.action().DROP() != null) {
            action = ForwardingAction.DROP;
         }
         else {
            throw new BatfishException(ERR_CONVERT_ACTION);
         }
      }
      else if (ctx.VARIABLE() != null) {
         action = _parameters.getAction(ctx.VARIABLE().getText().substring(1));
      }
      else {
         throw new BatfishException(ERR_CONVERT_ACTION);
      }
      return action;
   }

   private BgpNeighborExpr toBgpNeighborExpr(Bgp_neighbor_exprContext ctx) {
      if (ctx.BGP_NEIGHBOR() != null) {
         return BaseCaseBgpNeighborExpr.BGP_NEIGHBOR;
      }
      else if (ctx.var_bgp_neighbor_expr() != null) {
         return new VarBgpNeighborExpr(
               ctx.var_bgp_neighbor_expr().var.getText());
      }
      else {
         throw new BatfishException(ERR_CONVERT_BGP_NEIGHBOR);
      }
   }

   private BooleanExpr toBooleanExpr(And_exprContext expr) {
      Set<BooleanExpr> conjuncts = new HashSet<BooleanExpr>();
      for (Boolean_exprContext conjunctCtx : expr.conjuncts) {
         BooleanExpr conjunct = toBooleanExpr(conjunctCtx);
         conjuncts.add(conjunct);
      }
      AndExpr andExpr = new AndExpr(conjuncts);
      return andExpr;
   }

   private BooleanExpr toBooleanExpr(Bgp_neighbor_boolean_exprContext ctx) {
      BgpNeighborExpr caller = toBgpNeighborExpr(ctx.caller);
      if (ctx.bgp_neighbor_has_generated_route_boolean_expr() != null) {
         return new HasGeneratedRouteBgpNeighborBooleanExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(Boolean_exprContext ctx) {
      if (ctx.and_expr() != null) {
         return toBooleanExpr(ctx.and_expr());
      }
      else if (ctx.false_expr() != null) {
         return BaseCaseBooleanExpr.FALSE;
      }
      else if (ctx.eq_expr() != null) {
         return toBooleanExpr(ctx.eq_expr());
      }
      else if (ctx.ge_expr() != null) {
         return toBooleanExpr(ctx.ge_expr());
      }
      else if (ctx.gt_expr() != null) {
         return toBooleanExpr(ctx.gt_expr());
      }
      else if (ctx.if_expr() != null) {
         return toBooleanExpr(ctx.if_expr());
      }
      else if (ctx.le_expr() != null) {
         return toBooleanExpr(ctx.le_expr());
      }
      else if (ctx.lt_expr() != null) {
         return toBooleanExpr(ctx.lt_expr());
      }
      else if (ctx.neq_expr() != null) {
         return toBooleanExpr(ctx.neq_expr());
      }
      else if (ctx.not_expr() != null) {
         return toBooleanExpr(ctx.not_expr());
      }
      else if (ctx.or_expr() != null) {
         return toBooleanExpr(ctx.or_expr());
      }
      else if (ctx.property_boolean_expr() != null) {
         return toBooleanExpr(ctx.property_boolean_expr());
      }
      else if (ctx.set_contains_expr() != null) {
         return toBooleanExpr(ctx.set_contains_expr());
      }
      else if (ctx.true_expr() != null) {
         return BaseCaseBooleanExpr.TRUE;
      }
      else if (ctx.var_boolean_expr() != null) {
         return toBooleanExpr(ctx.var_boolean_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(Clause_boolean_exprContext ctx) {
      PolicyMapClauseExpr caller = toPolicyMapClauseExpr(ctx.caller);
      if (ctx.clause_permit_boolean_expr() != null) {
         return new PermitPolicyMapClauseBooleanExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(Eq_exprContext expr) {
      if (expr.lhs_int != null) {
         IntExpr lhs = toIntExpr(expr.lhs_int);
         IntExpr rhs = toIntExpr(expr.rhs_int);
         return new IntEqExpr(lhs, rhs);
      }
      else if (expr.lhs_string != null) {
         StringExpr lhs = toStringExpr(expr.lhs_string);
         StringExpr rhs = toStringExpr(expr.rhs_string);
         return new StringEqExpr(lhs, rhs);
      }
      else {
         throw new BatfishException("invalid eq_expr");
      }
   }

   private BooleanExpr toBooleanExpr(Ge_exprContext expr) {
      if (expr.lhs_int != null) {
         IntExpr lhs = toIntExpr(expr.lhs_int);
         IntExpr rhs = toIntExpr(expr.rhs_int);
         return new IntGeExpr(lhs, rhs);
      }
      else if (expr.lhs_string != null) {
         StringExpr lhs = toStringExpr(expr.lhs_string);
         StringExpr rhs = toStringExpr(expr.rhs_string);
         return new StringGeExpr(lhs, rhs);
      }
      else {
         throw new BatfishException("invalid ge_expr");
      }
   }

   private BooleanExpr toBooleanExpr(Gt_exprContext expr) {
      if (expr.lhs_int != null) {
         IntExpr lhs = toIntExpr(expr.lhs_int);
         IntExpr rhs = toIntExpr(expr.rhs_int);
         return new IntGtExpr(lhs, rhs);
      }
      else if (expr.lhs_string != null) {
         StringExpr lhs = toStringExpr(expr.lhs_string);
         StringExpr rhs = toStringExpr(expr.rhs_string);
         return new StringGtExpr(lhs, rhs);
      }
      else {
         throw new BatfishException("invalid gt_expr");
      }
   }

   private BooleanExpr toBooleanExpr(If_exprContext expr) {
      BooleanExpr antecedent = toBooleanExpr(expr.antecedent);
      BooleanExpr consequent = toBooleanExpr(expr.consequent);
      IfExpr ifExpr = new IfExpr(antecedent, consequent);
      return ifExpr;
   }

   private BooleanExpr toBooleanExpr(Interface_boolean_exprContext expr) {
      InterfaceExpr caller = toInterfaceExpr(expr.caller);
      if (expr.interface_enabled_boolean_expr() != null) {
         return new EnabledInterfaceBooleanExpr(caller);
      }
      else if (expr.interface_has_ip_boolean_expr() != null) {
         return new HasIpInterfaceBooleanExpr(caller);
      }
      else if (expr.interface_isis_boolean_expr() != null) {
         return toBooleanExpr(caller, expr.interface_isis_boolean_expr());
      }
      else if (expr.interface_isloopback_boolean_expr() != null) {
         return new IsLoopbackInterfaceBooleanExpr(caller);
      }
      else if (expr.interface_ospf_boolean_expr() != null) {
         return toBooleanExpr(caller, expr.interface_ospf_boolean_expr());
      }
      else {
         throw new BatfishException(ERR_CONVERT_BOOLEAN);
      }
   }

   private BooleanExpr toBooleanExpr(InterfaceExpr caller,
         Interface_isis_boolean_exprContext ctx) {
      if (ctx.interface_isis_l1_active_boolean_expr() != null) {
         return new IsisL1ActiveInterfaceBooleanExpr(caller);
      }
      else if (ctx.interface_isis_l1_passive_boolean_expr() != null) {
         return new IsisL1PassiveInterfaceBooleanExpr(caller);
      }
      else if (ctx.interface_isis_l2_active_boolean_expr() != null) {
         return new IsisL2ActiveInterfaceBooleanExpr(caller);
      }
      else if (ctx.interface_isis_l2_passive_boolean_expr() != null) {
         return new IsisL2PassiveInterfaceBooleanExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(InterfaceExpr caller,
         Interface_ospf_boolean_exprContext ctx) {
      if (ctx.interface_ospf_active_boolean_expr() != null) {
         return new OspfActiveInterfaceBooleanExpr(caller);
      }
      else if (ctx.interface_ospf_passive_boolean_expr() != null) {
         return new OspfPassiveInterfaceBooleanExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(Ipsec_vpn_boolean_exprContext expr) {
      IpsecVpnExpr caller = toIpsecVpnExpr(expr.ipsec_vpn_expr());
      if (expr.ipsec_vpn_compatible_ike_proposals_boolean_expr() != null) {
         return new CompatibleIkeProposalsIpsecVpnBooleanExpr(caller);
      }
      else if (expr.ipsec_vpn_compatible_ipsec_proposals_boolean_expr() != null) {
         return new CompatibleIpsecProposalsIpsecVpnBooleanExpr(caller);
      }
      else if (expr.ipsec_vpn_has_remote_ipsec_vpn_boolean_expr() != null) {
         return new HasRemoteIpsecVpnIpsecVpnBooleanExpr(caller);
      }
      else if (expr.ipsec_vpn_has_single_remote_ipsec_vpn_boolean_expr() != null) {
         return new HasSingleRemoteIpsecVpnIpsecVpnBooleanExpr(caller);
      }
      else {
         throw new BatfishException("Missing conversion for expression");
      }
   }

   private BooleanExpr toBooleanExpr(Le_exprContext expr) {
      if (expr.lhs_int != null) {
         IntExpr lhs = toIntExpr(expr.lhs_int);
         IntExpr rhs = toIntExpr(expr.rhs_int);
         return new IntLeExpr(lhs, rhs);
      }
      else if (expr.lhs_string != null) {
         StringExpr lhs = toStringExpr(expr.lhs_string);
         StringExpr rhs = toStringExpr(expr.rhs_string);
         return new StringLeExpr(lhs, rhs);
      }
      else {
         throw new BatfishException("invalid le_expr");
      }
   }

   private BooleanExpr toBooleanExpr(Line_boolean_exprContext ctx) {
      RouteFilterLineExpr caller = toRouteFilterLineExpr(ctx.caller);
      if (ctx.line_permit_boolean_expr() != null) {
         return new PermitLineBooleanExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(Lt_exprContext expr) {
      if (expr.lhs_int != null) {
         IntExpr lhs = toIntExpr(expr.lhs_int);
         IntExpr rhs = toIntExpr(expr.rhs_int);
         return new IntLtExpr(lhs, rhs);
      }
      else if (expr.lhs_string != null) {
         StringExpr lhs = toStringExpr(expr.lhs_string);
         StringExpr rhs = toStringExpr(expr.rhs_string);
         return new StringLtExpr(lhs, rhs);
      }
      else {
         throw new BatfishException("invalid lt_expr");
      }
   }

   private BooleanExpr toBooleanExpr(Neq_exprContext expr) {
      IntExpr lhs = toIntExpr(expr.lhs);
      IntExpr rhs = toIntExpr(expr.rhs);
      return new NeqExpr(lhs, rhs);
   }

   private BooleanExpr toBooleanExpr(Node_boolean_exprContext ctx) {
      NodeExpr caller = toNodeExpr(ctx.node_expr());
      if (ctx.node_bgp_boolean_expr() != null) {
         return toBooleanExpr(caller, ctx.node_bgp_boolean_expr());
      }
      else if (ctx.node_has_generated_route_boolean_expr() != null) {
         return new HasGeneratedRouteNodeBooleanExpr(caller);
      }
      else if (ctx.node_isis_boolean_expr() != null) {
         return toBooleanExpr(caller, ctx.node_isis_boolean_expr());
      }
      else if (ctx.node_ospf_boolean_expr() != null) {
         return toBooleanExpr(caller, ctx.node_ospf_boolean_expr());
      }
      else if (ctx.node_static_boolean_expr() != null) {
         return toBooleanExpr(caller, ctx.node_static_boolean_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(NodeExpr caller,
         Node_bgp_boolean_exprContext ctx) {
      if (ctx.node_bgp_configured_boolean_expr() != null) {
         return new BgpConfiguredNodeBooleanExpr(caller);
      }
      else if (ctx.node_bgp_has_generated_route_boolean_expr() != null) {
         return new BgpHasGeneratedRouteNodeBooleanExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(NodeExpr caller,
         Node_isis_boolean_exprContext ctx) {
      if (ctx.node_isis_configured_boolean_expr() != null) {
         return new IsisConfiguredNodeBooleanExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(NodeExpr caller,
         Node_ospf_boolean_exprContext ctx) {
      if (ctx.node_ospf_configured_boolean_expr() != null) {
         return new OspfConfiguredNodeBooleanExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(NodeExpr caller,
         Node_static_boolean_exprContext ctx) {
      if (ctx.node_static_configured_boolean_expr() != null) {
         return new StaticConfiguredNodeBooleanExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(Not_exprContext ctx) {
      BooleanExpr argument = toBooleanExpr(ctx.boolean_expr());
      NotExpr notExpr = new NotExpr(argument);
      return notExpr;
   }

   private BooleanExpr toBooleanExpr(Or_exprContext ctx) {
      Set<BooleanExpr> disjuncts = new HashSet<BooleanExpr>();
      for (Boolean_exprContext disjunctCtx : ctx.disjuncts) {
         BooleanExpr disjunct = toBooleanExpr(disjunctCtx);
         disjuncts.add(disjunct);
      }
      OrExpr orExpr = new OrExpr(disjuncts);
      return orExpr;
   }

   private BooleanExpr toBooleanExpr(Prefix_space_boolean_exprContext ctx) {
      PrefixSpaceExpr caller = toPrefixSpaceExpr(ctx.caller);
      if (ctx.prefix_space_overlaps_boolean_expr() != null) {
         return toBooleanExpr(caller, ctx.prefix_space_overlaps_boolean_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(PrefixSpaceExpr caller,
         Prefix_space_overlaps_boolean_exprContext ctx) {
      PrefixSpaceExpr arg = toPrefixSpaceExpr(ctx.arg);
      return new OverlapsPrefixSpaceBooleanExpr(caller, arg);
   }

   private BooleanExpr toBooleanExpr(Property_boolean_exprContext ctx) {
      if (ctx.bgp_neighbor_boolean_expr() != null) {
         return toBooleanExpr(ctx.bgp_neighbor_boolean_expr());
      }
      else if (ctx.clause_boolean_expr() != null) {
         return toBooleanExpr(ctx.clause_boolean_expr());
      }
      else if (ctx.interface_boolean_expr() != null) {
         return toBooleanExpr(ctx.interface_boolean_expr());
      }
      else if (ctx.ipsec_vpn_boolean_expr() != null) {
         return toBooleanExpr(ctx.ipsec_vpn_boolean_expr());
      }
      else if (ctx.line_boolean_expr() != null) {
         return toBooleanExpr(ctx.line_boolean_expr());
      }
      else if (ctx.node_boolean_expr() != null) {
         return toBooleanExpr(ctx.node_boolean_expr());
      }
      else if (ctx.prefix_space_boolean_expr() != null) {
         return toBooleanExpr(ctx.prefix_space_boolean_expr());
      }
      else if (ctx.static_route_boolean_expr() != null) {
         return toBooleanExpr(ctx.static_route_boolean_expr());
      }
      else {
         throw conversionError("Missing conversion for expression", ctx);
      }
   }

   private BooleanExpr toBooleanExpr(Set_contains_exprContext ctx) {
      Expr expr;
      if (ctx.ip_expr() != null) {
         expr = toIpExpr(ctx.ip_expr());
      }
      else if (ctx.expr() != null) {
         expr = toExpr(ctx.expr());
      }
      else if (ctx.route_filter_expr() != null) {
         expr = toRouteFilterExpr(ctx.route_filter_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_STATEMENT, ctx);
      }
      return new SetContainsExpr(expr, ctx.caller.getText(), ctx.type);
   }

   private BooleanExpr toBooleanExpr(Static_route_boolean_exprContext ctx) {
      StaticRouteExpr caller = toStaticRouteExpr(ctx.caller);
      if (ctx.static_route_has_next_hop_interface_boolean_expr() != null) {
         return new HasNextHopInterfaceStaticRouteBooleanExpr(caller);
      }
      else if (ctx.static_route_has_next_hop_ip_boolean_expr() != null) {
         return new HasNextHopIpStaticRouteBooleanExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_BOOLEAN, ctx);
      }
   }

   private BooleanExpr toBooleanExpr(Var_boolean_exprContext ctx) {
      String variable = ctx.VARIABLE().getText();
      return new VarBooleanExpr(variable);
   }

   private Expr toExpr(ExprContext ctx) {
      if (ctx.boolean_expr() != null) {
         return toBooleanExpr(ctx.boolean_expr());
      }
      else if (ctx.int_expr() != null) {
         return toIntExpr(ctx.int_expr());
      }
      else if (ctx.ip_expr() != null) {
         return toIpExpr(ctx.ip_expr());
      }
      else if (ctx.prefix_expr() != null) {
         return toPrefixExpr(ctx.prefix_expr());
      }
      else if (ctx.route_filter_line_expr() != null) {
         return toRouteFilterLineExpr(ctx.route_filter_line_expr());
      }
      else if (ctx.string_expr() != null) {
         return toStringExpr(ctx.string_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_PRINTABLE, ctx);
      }
   }

   private InterfaceExpr toInterfaceExpr(Interface_exprContext ctx) {
      if (ctx.INTERFACE() != null) {
         return BaseCaseInterfaceExpr.INTERFACE;
      }
      else if (ctx.var_interface_expr() != null) {
         return new VarInterfaceExpr(ctx.var_interface_expr().VARIABLE()
               .getText());
      }
      else {
         throw new BatfishException(ERR_CONVERT_INTERFACE);
      }
   }

   private IntExpr toIntExpr(Bgp_neighbor_int_exprContext ctx) {
      BgpNeighborExpr caller = toBgpNeighborExpr(ctx.caller);
      if (ctx.bgp_neighbor_local_as_int_expr() != null) {
         return new LocalAsBgpNeighborIntExpr(caller);
      }
      else if (ctx.bgp_neighbor_remote_as_int_expr() != null) {
         return new RemoteAsBgpNeighborIntExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_INT, ctx);
      }
   }

   private IntExpr toIntExpr(Int_exprContext ctx) {
      if (ctx.ASTERISK() != null) {
         IntExpr multiplicand1 = toIntExpr(ctx.multiplicand1);
         IntExpr multiplicand2 = toIntExpr(ctx.multiplicand2);
         return new ProductIntExpr(multiplicand1, multiplicand2);
      }
      else if (ctx.FORWARD_SLASH() != null) {
         IntExpr dividend = toIntExpr(ctx.dividend);
         IntExpr divisor = toIntExpr(ctx.divisor);
         return new QuotientIntExpr(dividend, divisor);
      }
      else if (ctx.MINUS() != null) {
         IntExpr subtrahend = toIntExpr(ctx.subtrahend);
         IntExpr minuend = toIntExpr(ctx.minuend);
         return new DifferenceIntExpr(subtrahend, minuend);
      }
      else if (ctx.OPEN_PAREN() != null) {
         return toIntExpr(ctx.parenthesized);
      }
      else if (ctx.PLUS() != null) {
         IntExpr addend1 = toIntExpr(ctx.addend1);
         IntExpr addend2 = toIntExpr(ctx.addend2);
         return new SumIntExpr(addend1, addend2);
      }
      else if (ctx.val_int_expr() != null) {
         return toIntExpr(ctx.val_int_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_INT, ctx);
      }
   }

   private IntExpr toIntExpr(Literal_int_exprContext ctx) {
      int i = Integer.parseInt(ctx.DEC().toString());
      return new LiteralIntExpr(i);
   }

   private IntExpr toIntExpr(Set_size_int_exprContext ctx) {
      String caller = ctx.caller.getText();
      VariableType type = ctx.type;
      return new SetSizeIntExpr(caller, type);
   }

   private IntExpr toIntExpr(Static_route_int_exprContext ctx) {
      StaticRouteExpr caller = toStaticRouteExpr(ctx.caller);
      if (ctx.static_route_administrative_cost_int_expr() != null) {
         return new AdministrativeCostStaticRouteIntExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_INT, ctx);
      }
   }

   private IntExpr toIntExpr(Val_int_exprContext ctx) {
      if (ctx.bgp_neighbor_int_expr() != null) {
         return toIntExpr(ctx.bgp_neighbor_int_expr());
      }
      else if (ctx.literal_int_expr() != null) {
         return toIntExpr(ctx.literal_int_expr());
      }
      else if (ctx.set_size_int_expr() != null) {
         return toIntExpr(ctx.set_size_int_expr());
      }
      else if (ctx.static_route_int_expr() != null) {
         return toIntExpr(ctx.static_route_int_expr());
      }
      else if (ctx.var_int_expr() != null) {
         return toIntExpr(ctx.var_int_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_INT, ctx);
      }
   }

   private IntExpr toIntExpr(Var_int_exprContext ctx) {
      String variable = ctx.VARIABLE().getText();
      return new VarIntExpr(variable);
   }

   private IpExpr toIpExpr(Bgp_neighbor_ip_exprContext ctx) {
      BgpNeighborExpr caller = toBgpNeighborExpr(ctx.caller);
      if (ctx.bgp_neighbor_local_ip_ip_expr() != null) {
         return new LocalIpBgpNeighborIpExpr(caller);
      }
      else if (ctx.bgp_neighbor_remote_ip_ip_expr() != null) {
         return new RemoteIpBgpNeighborIpExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_IP, ctx);
      }
   }

   private IpExpr toIpExpr(Interface_ip_exprContext ctx) {
      InterfaceExpr caller = toInterfaceExpr(ctx.caller);
      if (ctx.interface_ip_ip_expr() != null) {
         return new IpInterfaceIpExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_IP, ctx);
      }
   }

   private IpExpr toIpExpr(Ip_exprContext ctx) {
      if (ctx.bgp_neighbor_ip_expr() != null) {
         return toIpExpr(ctx.bgp_neighbor_ip_expr());
      }
      else if (ctx.interface_ip_expr() != null) {
         return toIpExpr(ctx.interface_ip_expr());
      }
      else if (ctx.static_route_ip_expr() != null) {
         return toIpExpr(ctx.static_route_ip_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_IP, ctx);
      }
   }

   private IpExpr toIpExpr(Static_route_ip_exprContext ctx) {
      StaticRouteExpr caller = toStaticRouteExpr(ctx.caller);
      if (ctx.static_route_next_hop_ip_ip_expr() != null) {
         return new NextHopIpStaticRouteIpExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_IP, ctx);
      }
   }

   private IpsecVpnExpr toIpsecVpnExpr(Ipsec_vpn_exprContext expr) {
      if (expr.IPSEC_VPN() != null) {
         return BaseCaseIpsecVpnExpr.IPSEC_VPN;
      }
      else if (expr.REMOTE_IPSEC_VPN() != null) {
         return BaseCaseIpsecVpnExpr.REMOTE_IPSEC_VPN;
      }
      else if (expr.ipsec_vpn_expr() != null) {
         return toIpsecVpnExpr(expr.ipsec_vpn_expr(),
               expr.ipsec_vpn_ipsec_vpn_expr());
      }
      else {
         throw new BatfishException("Missing conversion for expression");
      }
   }

   private IpsecVpnExpr toIpsecVpnExpr(Ipsec_vpn_exprContext callerCtx,
         Ipsec_vpn_ipsec_vpn_exprContext expr) {
      IpsecVpnExpr caller = toIpsecVpnExpr(callerCtx);
      if (expr.ipsec_vpn_remote_ipsec_vpn_ipsec_vpn_expr() != null) {
         return new RemoteIpsecVpnIpsecVpnExpr(caller);
      }
      else {
         throw new BatfishException("Missing conversion for expression");
      }
   }

   private NodeExpr toNodeExpr(Node_exprContext ctx) {
      if (ctx.NODE() != null) {
         return BaseCaseNodeExpr.NODE;
      }
      else if (ctx.var_node_expr() != null) {
         return new VarNodeExpr(ctx.var_node_expr().VARIABLE().getText());
      }
      else {
         throw new BatfishException("Missing conversion for expression");
      }

   }

   private PolicyMapClauseExpr toPolicyMapClauseExpr(Clause_exprContext ctx) {
      if (ctx.CLAUSE() != null) {
         return BaseCasePolicyMapClauseExpr.CLAUSE;
      }
      else {
         throw conversionError(ERR_CONVERT_POLICY_MAP_CLAUSE, ctx);
      }
   }

   private Prefix toPrefix(Ip_constraint_simpleContext ctx) {
      if (ctx.IP_ADDRESS() != null) {
         Ip ip = new Ip(ctx.IP_ADDRESS().getText());
         return new Prefix(ip, 32);
      }
      else if (ctx.IP_PREFIX() != null) {
         return new Prefix(ctx.IP_PREFIX().getText());
      }
      else {
         throw conversionError(
               "invalid simple ip constraint: " + ctx.getText(), ctx);
      }
   }

   private PrefixExpr toPrefixExpr(Generated_route_prefix_exprContext ctx) {
      if (ctx.generated_route_prefix_prefix_expr() != null) {
         return GeneratedRoutePrefixExpr.GENERATED_ROUTE_PREFIX;
      }
      else {
         throw conversionError(ERR_CONVERT_PREFIX, ctx);
      }
   }

   private PrefixExpr toPrefixExpr(Interface_prefix_exprContext ctx) {
      InterfaceExpr caller = toInterfaceExpr(ctx.caller);
      if (ctx.interface_prefix_prefix_expr() != null) {
         return new PrefixInterfacePrefixExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_PREFIX, ctx);
      }
   }

   private PrefixExpr toPrefixExpr(Prefix_exprContext ctx) {
      if (ctx.generated_route_prefix_expr() != null) {
         return toPrefixExpr(ctx.generated_route_prefix_expr());
      }
      else if (ctx.interface_prefix_expr() != null) {
         return toPrefixExpr(ctx.interface_prefix_expr());
      }
      else if (ctx.static_route_prefix_expr() != null) {
         return toPrefixExpr(ctx.static_route_prefix_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_PREFIX, ctx);
      }
   }

   private PrefixExpr toPrefixExpr(Static_route_prefix_exprContext ctx) {
      StaticRouteExpr caller = toStaticRouteExpr(ctx.caller);
      if (ctx.static_route_prefix_prefix_expr() != null) {
         return new PrefixStaticRoutePrefixExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_PREFIX, ctx);
      }
   }

   private Set<Prefix> toPrefixSet(Ip_constraint_complexContext ctx) {
      Set<Prefix> prefixes = new LinkedHashSet<Prefix>();
      for (Ip_constraint_simpleContext sctx : ctx.ip_constraint_simple()) {
         Prefix prefix = toPrefix(sctx);
         prefixes.add(prefix);
      }
      return prefixes;
   }

   private Set<Prefix> toPrefixSet(Ip_constraintContext ctx) {
      if (ctx.ip_constraint_complex() != null) {
         return toPrefixSet(ctx.ip_constraint_complex());
      }
      else if (ctx.ip_constraint_simple() != null) {
         return Collections.singleton(toPrefix(ctx.ip_constraint_simple()));
      }
      else if (ctx.VARIABLE() != null) {
         Set<Prefix> prefixes;
         String var = ctx.VARIABLE().getText().substring(1);
         VariableType type = _parameters.getTypeBindings().get(var);
         switch (type) {
         case SET_PREFIX:
            prefixes = _parameters.getPrefixSet(var);
            break;

         case PREFIX:
            Prefix prefix = _parameters.getPrefix(var);
            prefixes = Collections.singleton(prefix);
            break;

         case IP:
            Ip ip = _parameters.getIp(var);
            prefixes = Collections.singleton(new Prefix(ip, 32));
            break;
         // $CASES-OMITTED$
         default:
            throw new BatfishException("invalid constraint");
         }
         return prefixes;
      }
      else {
         throw new BatfishException("invalid ip constraint: " + ctx.getText());
      }
   }

   private PrefixSpaceExpr toPrefixSpaceExpr(Node_prefix_space_exprContext ctx) {
      NodeExpr caller = toNodeExpr(ctx.caller);
      if (ctx.node_bgp_origination_space_explicit_prefix_space_expr() != null) {
         return new BgpOriginationSpaceExplicitNodePrefixSpaceExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_PREFIX_SPACE, ctx);
      }
   }

   private PrefixSpaceExpr toPrefixSpaceExpr(Prefix_space_exprContext ctx) {
      if (ctx.node_prefix_space_expr() != null) {
         return toPrefixSpaceExpr(ctx.node_prefix_space_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_PREFIX_SPACE, ctx);
      }
   }

   private Set<SubRange> toRange(Range_constraintContext ctx) {
      if (ctx.range() != null) {
         return toRange(ctx.range());
      }
      else if (ctx.subrange() != null) {
         return Collections.singleton(toSubRange(ctx.subrange()));
      }
      else if (ctx.VARIABLE() != null) {
         String var = ctx.VARIABLE().getText().substring(1);
         VariableType type = _parameters.getTypeBindings().get(var);
         switch (type) {
         case INT:
            int value = (int) _parameters.getInt(var);
            return Collections.singleton(new SubRange(value, value));

         case RANGE:
            return _parameters.getRange(var);

            // $CASES-OMITTED$
         default:
            throw new BatfishException("invalid constraint");
         }
      }
      else {
         throw new BatfishException("invalid range constraint: "
               + ctx.getText());
      }
   }

   private Set<SubRange> toRange(RangeContext ctx) {
      Set<SubRange> range = new LinkedHashSet<SubRange>();
      for (SubrangeContext sctx : ctx.range_list) {
         SubRange subRange = toSubRange(sctx);
         range.add(subRange);
      }
      return range;
   }

   private String toRegex(Node_constraintContext ctx) {
      if (ctx.REGEX() != null) {
         String regex = toRegex(ctx.REGEX().getText());
         return regex;
      }
      else if (ctx.STRING_LITERAL() != null) {
         return Pattern.quote(ctx.STRING_LITERAL().getText());
      }
      else if (ctx.VARIABLE() != null) {
         String var = ctx.VARIABLE().getText().substring(1);
         VariableType type = _parameters.getTypeBindings().get(var);
         switch (type) {
         case REGEX:
            return toRegex(_parameters.getRegex(var));

         case STRING:
            String str = _parameters.getString(var);
            return Pattern.quote(str);

            // $CASES-OMITTED$
         default:
            throw new BatfishException("invalid constraint");
         }
      }
      else {
         throw new BatfishException("invalid node constraint: " + ctx.getText());
      }
   }

   private String toRegex(String text) {
      String prefix = "regex<";
      if (!text.startsWith(prefix) || !text.endsWith(">")) {
         throw new BatfishException("unexpected regex token text: " + text);
      }
      return text.substring(prefix.length(), text.length() - 1);
   }

   private RouteFilterExpr toRouteFilterExpr(Route_filter_exprContext ctx) {
      if (ctx.route_filter_route_filter_expr() != null) {
         return BaseCaseRouteFilterExpr.ROUTE_FILTER;
      }
      else {
         throw conversionError(ERR_CONVERT_ROUTE_FILTER, ctx);
      }
   }

   private RouteFilterLineExpr toRouteFilterLineExpr(
         Route_filter_line_exprContext ctx) {
      if (ctx.route_filter_line_line_expr() != null) {
         return BaseCaseRouterFilterLineExpr.LINE;
      }
      else {
         throw conversionError(ERR_CONVERT_ROUTE_FILTER_LINE, ctx);
      }
   }

   private Statement toStatement(AssertionContext ctx) {
      BooleanExpr expr = toBooleanExpr(ctx.boolean_expr());
      List<Statement> onErrorStatements = toStatements(ctx.statements);
      Assertion assertion = new Assertion(expr, ctx.getText(),
            onErrorStatements);
      return assertion;
   }

   private Statement toStatement(AssignmentContext ctx) {
      if (ctx.int_assignment() != null) {
         return toStatement(ctx.int_assignment());
      }
      else {
         throw conversionError(ERR_CONVERT_STATEMENT, ctx);
      }
   }

   private Statement toStatement(Foreach_bgp_neighbor_statementContext ctx) {
      return new ForEachBgpNeighborStatement(toStatements(ctx.statement()),
            null);
   }

   private Statement toStatement(Foreach_clause_statementContext ctx) {
      return new ForEachClauseStatement(toStatements(ctx.statement()), null);
   }

   private Statement toStatement(Foreach_generated_route_statementContext ctx) {
      return new ForEachGeneratedRouteStatement(toStatements(ctx.statement()));
   }

   private Statement toStatement(Foreach_interface_statementContext ctx) {
      return new ForEachInterfaceStatement(toStatements(ctx.statement()));
   }

   private Statement toStatement(Foreach_ipsec_vpn_statementContext ctx) {
      return new ForEachIpsecVpnStatement(toStatements(ctx.statement()));
   }

   private Statement toStatement(Foreach_line_statementContext ctx) {
      return new ForEachLineStatement(toStatements(ctx.statement()));
   }

   private Statement toStatement(Foreach_match_protocol_statementContext ctx) {
      return new ForEachMatchProtocolStatement(toStatements(ctx.statement()));
   }

   private Statement toStatement(Foreach_match_route_filter_statementContext ctx) {
      return new ForEachMatchRouteFilterStatement(toStatements(ctx.statement()));
   }

   private Statement toStatement(
         Foreach_node_bgp_generated_route_statementContext ctx) {
      return new ForEachNodeBgpGeneratedRouteStatement(
            toStatements(ctx.statement()));
   }

   private Statement toStatement(Foreach_node_statementContext ctx) {
      String var = null;
      if (ctx.var != null) {
         var = ctx.var.getText();
      }
      return new ForEachNodeStatement(toStatements(ctx.statement()), var);
   }

   private Statement toStatement(
         Foreach_ospf_outbound_policy_statementContext ctx) {
      return new ForEachOspfOutboundPolicyStatement(
            toStatements(ctx.statement()));
   }

   private Statement toStatement(Foreach_protocol_statementContext ctx) {
      return new ForEachProtocolStatement(toStatements(ctx.statement()));
   }

   private Statement toStatement(Foreach_remote_ipsec_vpn_statementContext ctx) {
      return new ForEachRemoteIpsecVpnStatement(toStatements(ctx.statement()));
   }

   private Statement toStatement(
         Foreach_route_filter_in_set_statementContext ctx) {
      List<Statement> statements = toStatements(ctx.statement());
      String set = ctx.set.getText();
      return new ForEachRouteFilterInSetStatement(statements, set);
   }

   private Statement toStatement(Foreach_route_filter_statementContext ctx) {
      return new ForEachRouteFilterStatement(toStatements(ctx.statement()));
   }

   private Statement toStatement(Foreach_static_route_statementContext ctx) {
      return new ForEachStaticRouteStatement(toStatements(ctx.statement()));

   }

   private Statement toStatement(If_statementContext ctx) {
      BooleanExpr guard = toBooleanExpr(ctx.guard);
      List<Statement> trueStatements = toStatements(ctx.true_statements);
      List<Statement> falseStatements = toStatements(ctx.false_statements);
      return new IfStatement(guard, trueStatements, falseStatements);
   }

   private Statement toStatement(Int_assignmentContext ctx) {
      String variable = ctx.VARIABLE().getText();
      IntExpr intExpr = toIntExpr(ctx.int_expr());
      return new IntegerAssignment(variable, intExpr);
   }

   private Statement toStatement(MethodContext ctx) {
      if (ctx.typed_method() != null) {
         return toStatement(ctx.typed_method());
      }
      else {
         throw conversionError(ERR_CONVERT_STATEMENT, ctx);
      }
   }

   private Statement toStatement(Printf_statementContext ctx) {
      StringExpr formatString = toStringExpr(ctx.format_string);
      List<Expr> replacements = new ArrayList<Expr>();
      for (ExprContext pexpr : ctx.replacements) {
         Expr replacement = toExpr(pexpr);
         replacements.add(replacement);
      }
      return new PrintfStatement(formatString, replacements);
   }

   private Statement toStatement(Set_add_methodContext ctx) {
      Expr expr;
      if (ctx.ip_expr() != null) {
         expr = toIpExpr(ctx.ip_expr());
      }
      else if (ctx.expr() != null) {
         expr = toExpr(ctx.expr());
      }
      else if (ctx.route_filter_expr() != null) {
         expr = toRouteFilterExpr(ctx.route_filter_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_STATEMENT, ctx);
      }
      return new SetAddStatement(expr, ctx.caller, ctx.type);
   }

   private Statement toStatement(Set_clear_methodContext ctx) {
      return new SetClearStatement(ctx.caller, ctx.type);
   }

   private Statement toStatement(Set_declaration_statementContext ctx) {
      String var = ctx.var.getText();
      VariableType type = VariableType.fromString(ctx.typeStr);
      return new SetDeclarationStatement(var, type);
   }

   private Statement toStatement(StatementContext ctx) {
      if (ctx.assertion() != null) {
         return toStatement(ctx.assertion());
      }
      else if (ctx.assignment() != null) {
         return toStatement(ctx.assignment());
      }
      else if (ctx.foreach_bgp_neighbor_statement() != null) {
         return toStatement(ctx.foreach_bgp_neighbor_statement());
      }
      else if (ctx.foreach_clause_statement() != null) {
         return toStatement(ctx.foreach_clause_statement());
      }
      else if (ctx.foreach_generated_route_statement() != null) {
         return toStatement(ctx.foreach_generated_route_statement());
      }
      else if (ctx.foreach_interface_statement() != null) {
         return toStatement(ctx.foreach_interface_statement());
      }
      else if (ctx.foreach_ipsec_vpn_statement() != null) {
         return toStatement(ctx.foreach_ipsec_vpn_statement());
      }
      else if (ctx.foreach_line_statement() != null) {
         return toStatement(ctx.foreach_line_statement());
      }
      else if (ctx.foreach_match_protocol_statement() != null) {
         return toStatement(ctx.foreach_match_protocol_statement());
      }
      else if (ctx.foreach_match_route_filter_statement() != null) {
         return toStatement(ctx.foreach_match_route_filter_statement());
      }
      else if (ctx.foreach_node_bgp_generated_route_statement() != null) {
         return toStatement(ctx.foreach_node_bgp_generated_route_statement());
      }
      else if (ctx.foreach_node_statement() != null) {
         return toStatement(ctx.foreach_node_statement());
      }
      else if (ctx.foreach_ospf_outbound_policy_statement() != null) {
         return toStatement(ctx.foreach_ospf_outbound_policy_statement());
      }
      else if (ctx.foreach_protocol_statement() != null) {
         return toStatement(ctx.foreach_protocol_statement());
      }
      else if (ctx.foreach_remote_ipsec_vpn_statement() != null) {
         return toStatement(ctx.foreach_remote_ipsec_vpn_statement());
      }
      else if (ctx.foreach_route_filter_in_set_statement() != null) {
         return toStatement(ctx.foreach_route_filter_in_set_statement());
      }
      else if (ctx.foreach_route_filter_statement() != null) {
         return toStatement(ctx.foreach_route_filter_statement());
      }
      else if (ctx.foreach_static_route_statement() != null) {
         return toStatement(ctx.foreach_static_route_statement());
      }
      else if (ctx.if_statement() != null) {
         return toStatement(ctx.if_statement());
      }
      else if (ctx.method() != null) {
         return toStatement(ctx.method());
      }
      else if (ctx.printf_statement() != null) {
         return toStatement(ctx.printf_statement());
      }
      else if (ctx.set_declaration_statement() != null) {
         return toStatement(ctx.set_declaration_statement());
      }
      else if (ctx.unless_statement() != null) {
         return toStatement(ctx.unless_statement());
      }
      else {
         throw conversionError(ERR_CONVERT_STATEMENT, ctx);
      }
   }

   private Statement toStatement(Typed_methodContext ctx) {
      if (ctx.set_add_method() != null) {
         return toStatement(ctx.set_add_method());
      }
      else if (ctx.set_clear_method() != null) {
         return toStatement(ctx.set_clear_method());
      }
      else {
         throw conversionError(ERR_CONVERT_STATEMENT, ctx);
      }
   }

   private Statement toStatement(Unless_statementContext ctx) {
      BooleanExpr guard = toBooleanExpr(ctx.guard);
      List<Statement> statements = toStatements(ctx.statements);
      return new UnlessStatement(guard, statements);
   }

   private List<Statement> toStatements(List<StatementContext> sctxList) {
      List<Statement> statements = new ArrayList<Statement>();
      for (StatementContext sctx : sctxList) {
         Statement statement = toStatement(sctx);
         statements.add(statement);
      }
      return statements;
   }

   private StaticRouteExpr toStaticRouteExpr(Static_route_exprContext ctx) {
      if (ctx.STATIC_ROUTE() != null) {
         return BaseCaseStaticRouteExpr.STATIC_ROUTE;
      }
      else if (ctx.var_static_route_expr() != null) {
         return new VarStaticRouteExpr(ctx.var_static_route_expr().VARIABLE()
               .getText());
      }
      else {
         throw new BatfishException(ERR_CONVERT_STATIC_ROUTE);
      }
   }

   private StringExpr toStringExpr(Interface_string_exprContext ctx) {
      if (ctx.interface_name_string_expr() != null) {
         return InterfaceStringExpr.INTERFACE_NAME;
      }
      else {
         throw conversionError(ERR_CONVERT_STRING, ctx);
      }
   }

   private StringExpr toStringExpr(Ipsec_vpn_string_exprContext ctx) {
      IpsecVpnExpr caller = toIpsecVpnExpr(ctx.ipsec_vpn_expr());
      if (ctx.ipsec_vpn_ike_gateway_name_string_expr() != null) {
         return new IkeGatewayNameIpsecVpnStringExpr(caller);
      }
      else if (ctx.ipsec_vpn_ike_policy_name_string_expr() != null) {
         return new IkePolicyNameIpsecVpnStringExpr(caller);
      }
      else if (ctx.ipsec_vpn_ipsec_policy_name_string_expr() != null) {
         return new IpsecPolicyNameIpsecVpnStringExpr(caller);
      }
      else if (ctx.ipsec_vpn_name_string_expr() != null) {
         return new NameIpsecVpnStringExpr(caller);
      }
      else if (ctx.ipsec_vpn_owner_name_string_expr() != null) {
         return new OwnerNameIpsecVpnStringExpr(caller);
      }
      else if (ctx.ipsec_vpn_pre_shared_key_hash_string_expr() != null) {
         return new PreSharedKeyHashIpsecVpnStringExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_STRING, ctx);
      }
   }

   private StringExpr toStringExpr(Node_string_exprContext ctx) {
      NodeExpr caller = toNodeExpr(ctx.node_expr());
      if (ctx.node_name_string_expr() != null) {
         return new NameNodeStringExpr(caller);
      }
      else {
         throw conversionError(ERR_CONVERT_STRING, ctx);
      }
   }

   private StringExpr toStringExpr(Protocol_string_exprContext ctx) {
      if (ctx.protocol_name_string_expr() != null) {
         return ProtocolStringExpr.PROTOCOL_NAME;
      }
      else {
         throw conversionError(ERR_CONVERT_STRING, ctx);
      }
   }

   private StringExpr toStringExpr(Route_filter_string_exprContext ctx) {
      if (ctx.route_filter_name_string_expr() != null) {
         return RouteFilterStringExpr.NAME;
      }
      else {
         throw conversionError(ERR_CONVERT_STRING, ctx);
      }
   }

   private StringExpr toStringExpr(Static_route_string_exprContext ctx) {
      if (ctx.static_route_next_hop_interface_string_expr() != null) {
         return StaticRouteStringExpr.STATICROUTE_NEXT_HOP_INTERFACE;
      }
      else {
         throw conversionError(ERR_CONVERT_STRING, ctx);
      }
   }

   private StringExpr toStringExpr(String_exprContext ctx) {
      if (ctx.interface_string_expr() != null) {
         return toStringExpr(ctx.interface_string_expr());
      }
      else if (ctx.ipsec_vpn_string_expr() != null) {
         return toStringExpr(ctx.ipsec_vpn_string_expr());
      }
      else if (ctx.node_string_expr() != null) {
         return toStringExpr(ctx.node_string_expr());
      }
      else if (ctx.protocol_string_expr() != null) {
         return toStringExpr(ctx.protocol_string_expr());
      }
      else if (ctx.static_route_string_expr() != null) {
         return toStringExpr(ctx.static_route_string_expr());
      }
      else if (ctx.route_filter_string_expr() != null) {
         return toStringExpr(ctx.route_filter_string_expr());
      }
      else if (ctx.string_literal_string_expr() != null) {
         return toStringExpr(ctx.string_literal_string_expr());
      }
      else {
         throw conversionError(ERR_CONVERT_STRING, ctx);
      }
   }

   private StringExpr toStringExpr(String_literal_string_exprContext ctx) {
      String withEscapes = ctx.getText();
      String processed = Util.unescapeJavaString(withEscapes);
      return new StringLiteralStringExpr(processed);
   }

   private SubRange toSubRange(SubrangeContext ctx) {
      int low = Integer.parseInt(ctx.low.getText());
      if (ctx.high != null) {
         int high = Integer.parseInt(ctx.high.getText());
         return new SubRange(low, high);
      }
      else {
         return new SubRange(low, low);
      }
   }

}
