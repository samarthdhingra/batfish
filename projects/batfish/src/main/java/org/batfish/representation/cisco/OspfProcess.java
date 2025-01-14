package org.batfish.representation.cisco;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.batfish.common.BatfishException;
import org.batfish.datamodel.ConfigurationFormat;
import org.batfish.datamodel.Ip;
import org.batfish.datamodel.Prefix;
import org.batfish.datamodel.RoutingProtocol;
import org.batfish.datamodel.ospf.OspfAreaSummary;
import org.batfish.datamodel.ospf.OspfMetricType;

public class OspfProcess implements Serializable {

  private static final long DEFAULT_DEFAULT_INFORMATION_METRIC = 1L;

  private static final OspfMetricType DEFAULT_DEFAULT_INFORMATION_METRIC_TYPE = OspfMetricType.E2;

  // Although not clearly documented; from GNS3 emulation and Cisco forum
  // (https://community.cisco.com/t5/switching/ospf-cost-calculation/td-p/2917356)
  public static final int DEFAULT_LOOPBACK_OSPF_COST = 1;

  public static final long DEFAULT_MAX_METRIC_EXTERNAL_LSA = 0xFF0000L;

  public static final long DEFAULT_MAX_METRIC_SUMMARY_LSA = 0xFF0000L;

  private static final double DEFAULT_REFERENCE_BANDWIDTH_100_MBPS = 100E6D;

  public static final long MAX_METRIC_ROUTER_LSA = 0xFFFFL;

  private long _defaultInformationMetric;

  private OspfMetricType _defaultInformationMetricType;

  private boolean _defaultInformationOriginate;

  private boolean _defaultInformationOriginateAlways;

  private String _defaultInformationOriginateMap;

  private Long _defaultMetric;

  @Nullable private DistributeList _inboundGlobalDistributeList;

  @Nonnull private Map<String, DistributeList> _inboundIInterfaceDistributeLists;

  private Long _maxMetricExternalLsa;

  private boolean _maxMetricIncludeStub;

  private boolean _maxMetricRouterLsa;

  private Long _maxMetricSummaryLsa;

  private final String _name;

  private Set<String> _nonDefaultInterfaces;

  private Map<Long, NssaSettings> _nssas;

  private Map<Long, StubSettings> _stubs;

  @Nullable private DistributeList _outboundGlobalDistributeList;

  @Nonnull private Map<String, DistributeList> _outboundInterfaceDistributeLists;

  private boolean _passiveInterfaceDefault;

  private Set<String> _passiveInterfaces;

  private Map<RoutingProtocolInstance, OspfRedistributionPolicy> _redistributionPolicies;

  private double _referenceBandwidth;

  private @Nullable Boolean _rfc1583Compatible;

  private Ip _routerId;

  private Map<Long, Map<Prefix, OspfAreaSummary>> _summaries;

  private Set<OspfWildcardNetwork> _wildcardNetworks;

  public static double getReferenceOspfBandwidth(ConfigurationFormat format) {
    switch (format) {
      case ARUBAOS:
      case CADANT: // Internet claims they use the Cisco defaults.
      case CISCO_IOS: // https://www.cisco.com/c/en/us/td/docs/ios-xml/ios/iproute_ospf/command/iro-cr-book/ospf-a1.html#wp3271966058
      case FORCE10: // http://www.dell.com/support/manuals/us/en/19/force10-s4810/s4810_9.9.0.0_cli_pub/auto-cost
      case FOUNDRY: // http://www.brocade.com/content/html/en/command-reference-guide/FI_08030_CMDREF/GUID-D7109E43-D368-46FE-95AF-D522B203E501.html
        return DEFAULT_REFERENCE_BANDWIDTH_100_MBPS;

      default:
        throw new BatfishException("Unknown default OSPF reference bandwidth for format " + format);
    }
  }

  public long getDefaultMetric(ConfigurationFormat format, RoutingProtocol protocol) {
    if (_defaultMetric != null) {
      return _defaultMetric;
    }

    switch (format) {
      case ARUBAOS:
      case CADANT: // Vetted IOS; assuming the rest use IOS defaults.
      case CISCO_IOS:
      case FORCE10:
      case FOUNDRY:
        // https://www.cisco.com/c/en/us/support/docs/ip/open-shortest-path-first-ospf/7039-1.html
        // "the cost allocated to the external route is 20 (the default is 1 for BGP)."
        switch (protocol) {
          case BGP:
            return 1;
          default:
            return 20;
        }

      default:
        throw new BatfishException("Unknown default OSPF reference bandwidth for format " + format);
    }
  }

  public OspfProcess(String name, ConfigurationFormat format) {
    _name = name;
    _referenceBandwidth = getReferenceOspfBandwidth(format);
    _defaultInformationMetric = DEFAULT_DEFAULT_INFORMATION_METRIC;
    _defaultInformationMetricType = DEFAULT_DEFAULT_INFORMATION_METRIC_TYPE;
    _inboundIInterfaceDistributeLists = new HashMap<>();
    _nonDefaultInterfaces = new HashSet<>();
    _nssas = new HashMap<>();
    _outboundInterfaceDistributeLists = new HashMap<>();
    _passiveInterfaces = new HashSet<>();
    _stubs = new HashMap<>();
    _wildcardNetworks = new TreeSet<>();
    _redistributionPolicies = new HashMap<>();
    _summaries = new TreeMap<>();
  }

  public long getDefaultInformationMetric() {
    return _defaultInformationMetric;
  }

  public OspfMetricType getDefaultInformationMetricType() {
    return _defaultInformationMetricType;
  }

  public boolean getDefaultInformationOriginate() {
    return _defaultInformationOriginate;
  }

  public boolean getDefaultInformationOriginateAlways() {
    return _defaultInformationOriginateAlways;
  }

  public String getDefaultInformationOriginateMap() {
    return _defaultInformationOriginateMap;
  }

  public Long getDefaultMetric() {
    return _defaultMetric;
  }

  @Nullable
  public DistributeList getInboundGlobalDistributeList() {
    return _inboundGlobalDistributeList;
  }

  @Nonnull
  public Map<String, DistributeList> getInboundInterfaceDistributeLists() {
    return _inboundIInterfaceDistributeLists;
  }

  public Long getMaxMetricExternalLsa() {
    return _maxMetricExternalLsa;
  }

  public boolean getMaxMetricIncludeStub() {
    return _maxMetricIncludeStub;
  }

  public boolean getMaxMetricRouterLsa() {
    return _maxMetricRouterLsa;
  }

  public Long getMaxMetricSummaryLsa() {
    return _maxMetricSummaryLsa;
  }

  public String getName() {
    return _name;
  }

  public Set<String> getNonDefaultInterfaces() {
    return _nonDefaultInterfaces;
  }

  public Map<Long, NssaSettings> getNssas() {
    return _nssas;
  }

  @Nullable
  public DistributeList getOutboundGlobalDistributeList() {
    return _outboundGlobalDistributeList;
  }

  @Nonnull
  public Map<String, DistributeList> getOutboundInterfaceDistributeLists() {
    return _outboundInterfaceDistributeLists;
  }

  public boolean getPassiveInterfaceDefault() {
    return _passiveInterfaceDefault;
  }

  public Set<String> getPassiveInterfaces() {
    return _passiveInterfaces;
  }

  public Map<RoutingProtocolInstance, OspfRedistributionPolicy> getRedistributionPolicies() {
    return _redistributionPolicies;
  }

  public double getReferenceBandwidth() {
    return _referenceBandwidth;
  }

  public @Nullable Boolean getRfc1583Compatible() {
    return _rfc1583Compatible;
  }

  public Ip getRouterId() {
    return _routerId;
  }

  public Map<Long, StubSettings> getStubs() {
    return _stubs;
  }

  public Map<Long, Map<Prefix, OspfAreaSummary>> getSummaries() {
    return _summaries;
  }

  public Set<OspfWildcardNetwork> getWildcardNetworks() {
    return _wildcardNetworks;
  }

  public void setDefaultInformationMetric(int metric) {
    _defaultInformationMetric = metric;
  }

  public void setDefaultInformationMetricType(OspfMetricType metricType) {
    _defaultInformationMetricType = metricType;
  }

  public void setDefaultInformationOriginate(boolean b) {
    _defaultInformationOriginate = b;
  }

  public void setDefaultInformationOriginateAlways(boolean b) {
    _defaultInformationOriginateAlways = b;
  }

  public void setDefaultInformationOriginateMap(String name) {
    _defaultInformationOriginateMap = name;
  }

  public void setDefaultMetric(Long metric) {
    _defaultMetric = metric;
  }

  public void setInboundGlobalDistributeList(@Nullable DistributeList inboundGlobalDistributeList) {
    _inboundGlobalDistributeList = inboundGlobalDistributeList;
  }

  public void setMaxMetricExternalLsa(Long maxMetricExternalLsa) {
    _maxMetricExternalLsa = maxMetricExternalLsa;
  }

  public void setMaxMetricIncludeStub(boolean maxMetricIncludeStub) {
    _maxMetricIncludeStub = maxMetricIncludeStub;
  }

  public void setMaxMetricRouterLsa(boolean maxMetricRouterLsa) {
    _maxMetricRouterLsa = maxMetricRouterLsa;
  }

  public void setMaxMetricSummaryLsa(Long maxMetricSummaryLsa) {
    _maxMetricSummaryLsa = maxMetricSummaryLsa;
  }

  public void setOutboundGlobalDistributeList(
      @Nullable DistributeList outboundGlobalDistributeList) {
    _outboundGlobalDistributeList = outboundGlobalDistributeList;
  }

  public void setPassiveInterfaceDefault(boolean b) {
    _passiveInterfaceDefault = b;
  }

  public void setReferenceBandwidth(double referenceBandwidth) {
    _referenceBandwidth = referenceBandwidth;
  }

  public void setRfc1583Compatible(@Nullable Boolean rfc1583Compatible) {
    _rfc1583Compatible = rfc1583Compatible;
  }

  public void setRouterId(Ip routerId) {
    _routerId = routerId;
  }
}
