package org.batfish.question.snmpcommunityclients;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.batfish.datamodel.UniverseIpSpace;
import org.batfish.datamodel.questions.Question;
import org.batfish.specifier.AllNodesNodeSpecifier;
import org.batfish.specifier.ConstantIpSpaceSpecifier;
import org.batfish.specifier.IpSpaceSpecifier;
import org.batfish.specifier.NodeSpecifier;
import org.batfish.specifier.SpecifierFactories;

/**
 * A question to check if devices have an SNMP community that permits specified client IP space.
 *
 * <p>It does not support all device vendors. See {@link
 * SnmpCommunityClientsAnswerer#isConfigurationSupported}
 */
@ParametersAreNonnullByDefault
public final class SnmpCommunityClientsQuestion extends Question {
  private static final String PROP_COMMUNITY = "community";
  private static final String PROP_CLIENTS = "clients";
  private static final String PROP_NODES = "nodes";

  private static final IpSpaceSpecifier DEFAULT_CLIENTS_IPSPACE_SPECIFIER =
      new ConstantIpSpaceSpecifier(UniverseIpSpace.INSTANCE);
  private static final NodeSpecifier DEFAULT_NODE_SPECIFIER = AllNodesNodeSpecifier.INSTANCE;

  @Nullable private final String _community;
  @Nullable private final String _clients;
  @Nullable private final String _nodes;

  SnmpCommunityClientsQuestion() {
    this(null, null, null);
  }

  SnmpCommunityClientsQuestion(
      @Nullable String community, @Nullable String clients, @Nullable String nodes) {
    _community = community;
    _clients = clients;
    _nodes = nodes;
  }

  @JsonCreator
  private static SnmpCommunityClientsQuestion jsonCreator(
      @JsonProperty(PROP_COMMUNITY) @Nullable String community,
      @JsonProperty(PROP_CLIENTS) @Nullable String clients,
      @JsonProperty(PROP_NODES) @Nullable String nodes) {
    return new SnmpCommunityClientsQuestion(community, clients, nodes);
  }

  @Override
  public boolean getDataPlane() {
    return false;
  }

  @Override
  public String getName() {
    return "snmpCommunityClients";
  }

  @Nullable
  @JsonProperty(PROP_COMMUNITY)
  public String getCommunity() {
    return _community;
  }

  @Nullable
  @JsonProperty(PROP_CLIENTS)
  public String getClients() {
    return _clients;
  }

  @Nonnull
  @JsonIgnore
  IpSpaceSpecifier getClientsIpSpaceSpecifier() {
    return SpecifierFactories.getIpSpaceSpecifierOrDefault(
        _clients, DEFAULT_CLIENTS_IPSPACE_SPECIFIER);
  }

  @Nullable
  @JsonProperty(PROP_NODES)
  public String getNodes() {
    return _nodes;
  }

  @Nonnull
  @JsonIgnore
  NodeSpecifier getNodeSpecifier() {
    return SpecifierFactories.getNodeSpecifierOrDefault(_nodes, DEFAULT_NODE_SPECIFIER);
  }
}
