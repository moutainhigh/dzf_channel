package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.ArrayListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.channel.dealmanage.GoodsBillBVO;
import com.dzf.model.channel.dealmanage.GoodsBillVO;
import com.dzf.model.channel.invoice.BillingInvoiceVO;
import com.dzf.model.channel.invoice.ChInvoiceBVO;
import com.dzf.model.piaotong.PiaoTongInvBVO;
import com.dzf.model.piaotong.PiaoTongInvVO;
import com.dzf.model.piaotong.PiaoTongResBVO;
import com.dzf.model.piaotong.PiaoTongResVO;
import com.dzf.model.piaotong.invinfo.InvInfoResBVO;
import com.dzf.model.piaotong.invinfo.QueryInvInfoVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IDefaultValue;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.InvManagerService;
import com.dzf.service.piaotong.IPiaoTongConstant;
import com.dzf.service.piaotong.PiaoTongBill;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;
import com.itextpdf.xmp.impl.Base64;

@Service("invManagerService")
public class InvManagerServiceImpl implements InvManagerService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private IPubService pubser;
	
	@Autowired
	private IUserService userServiceImpl;

	private final static String tablename = "cn_invoice";

	@SuppressWarnings("unchecked")
	@Override
	public List<ChInvoiceVO> query(ChInvoiceVO paramvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(paramvo);
		List<ChInvoiceVO> retlist = (List<ChInvoiceVO>) multBodyObjectBO.queryDataPage(ChInvoiceVO.class,
				qryvo.getSql(), qryvo.getSpm(), paramvo.getPage(), paramvo.getRows(), "t.ts");
		if (retlist != null && retlist.size() > 0) {
			setShowName(paramvo, retlist);
		}
		return retlist;
	}

	/**
	 * 设置显示名称
	 * 
	 * @param paramvo
	 * @param retlist
	 * @throws DZFWarpException
	 */
	private void setShowName(ChInvoiceVO paramvo, List<ChInvoiceVO> retlist) throws DZFWarpException {
		Map<Integer, String> areaMap = pubser.getAreaMap(paramvo.getAreaname(), 3);
		UserVO uservo = null;
		StringBuffer vmemo = null;
		Map<String, UserVO> marmap = pubser.getManagerMap(1);// 渠道经理
		Map<String, UserVO> opermap = pubser.getManagerMap(3);// 渠道运营
		HashMap<String, UserVO> map = userServiceImpl.queryUserMap(IDefaultValue.DefaultGroup, true);
		for (ChInvoiceVO vo : retlist) {
			if (areaMap != null && !areaMap.isEmpty()) {
				String area = areaMap.get(vo.getVprovince());
				if (!StringUtil.isEmpty(area)) {
					vo.setAreaname(area);// 大区名称
				}
			}
			vmemo = new StringBuffer();
			if (vo.getDchangedate() != null) {
				vmemo.append(vo.getDchangedate());
			}
			vmemo.append(" ");//为前端添加已换票图片处理
			if (!StringUtil.isEmpty(vo.getVchangememo())) {
				vmemo.append(vo.getVchangememo());
			}
			vo.setVchangememo(vmemo.toString());// 换票说明
			uservo = map.get(vo.getInvperson());
			if (uservo != null) {
				vo.setIperson(uservo.getUser_name());// 开票人
			}

			if (marmap != null && !marmap.isEmpty()) {
				uservo = marmap.get(vo.getPk_corp());
				if (uservo != null) {
					vo.setVmanager(uservo.getUser_name());// 渠道经理
				}
			}
			if (opermap != null && !opermap.isEmpty()) {
				uservo = opermap.get(vo.getPk_corp());
				if (uservo != null) {
					vo.setVoperater(uservo.getUser_name());// 渠道运营
				}
			}
		}
	}

	@Override
	public Integer queryTotalRow(ChInvoiceVO paramvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(paramvo);
		return multBodyObjectBO.queryDataTotal(ChInvoiceVO.class, qryvo.getSql(), qryvo.getSpm());
	}

	/**
	 * 获取查询条件
	 * 
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getQrySql(ChInvoiceVO paramvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select t.*, ba.vprovince  \n");
		sql.append("  from cn_invoice t  \n");
		sql.append("  left join bd_account ba on t.pk_corp = ba.pk_corp  \n");
		sql.append(" where nvl(t.dr, 0) = 0  \n");
		sql.append("   and nvl(ba.dr, 0) = 0  \n");
		if (!StringUtil.isEmpty(paramvo.getVprovname())) {
			sql.append(paramvo.getVprovname());
		}
		if (paramvo.getInvstatus() != null && paramvo.getInvstatus() == 9) {
			sql.append(" and t.dchangedate is not null");
		} else if (paramvo.getInvstatus() != null && paramvo.getInvstatus() != -1) {
			sql.append(" and t.invstatus = ?");
			spm.addParam(paramvo.getInvstatus());
		} else {
			sql.append(" and t.invstatus in (1,2,3)");
		}
		if (paramvo.getInvtype() != null && paramvo.getInvtype() != -1) {
			sql.append(" and t.invtype = ?");
			spm.addParam(paramvo.getInvtype());
		}
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 1) {
			if (!StringUtil.isEmpty(paramvo.getBdate())) {
				sql.append(" and t.apptime >= ?");
				spm.addParam(paramvo.getBdate());
			}
			if (!StringUtil.isEmpty(paramvo.getEdate())) {
				sql.append(" and t.apptime <= ?");
				spm.addParam(paramvo.getEdate());
			}
		} else {
			if (!StringUtil.isEmpty(paramvo.getBdate())) {
				sql.append(" and t.invtime >= ?");
				spm.addParam(paramvo.getBdate());
			}
			if (!StringUtil.isEmpty(paramvo.getEdate())) {
				sql.append(" and t.invtime <= ?");
				spm.addParam(paramvo.getEdate());
			}
		}

		// 发票来源类型 1：合同扣款开票； 2：商品扣款开票；
		if (paramvo.getIsourcetype() != null && paramvo.getIsourcetype() != -1) {
			sql.append(" and t.isourcetype = ?");
			spm.addParam(paramvo.getIsourcetype());
		}

		if (paramvo.getCorps() != null && paramvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
			sql.append(" and t.pk_corp  in (" + corpIdS + ")");
		}
		if (!StringUtil.isEmpty(paramvo.getCorpname())) {
			sql.append(" and t.corpname like ?");
			spm.addParam("%" + paramvo.getCorpname() + "%");
		}
		if (paramvo.getIpaytype() != null && paramvo.getIpaytype() != -1) {
			sql.append(" and t.ipaytype = ?");
			spm.addParam(paramvo.getIpaytype());
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public Integer queryChTotalRow(ChInvoiceVO vo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select count(1) from bd_corp where nvl(dr,0) = 0 and nvl(isaccountcorp,'N') = 'Y'");
		sql.append("  and nvl(ischannel,'N') = 'Y' and nvl(isseal,'N')='N'");
		if (!StringUtil.isEmpty(vo.getCorpcode())) {
			sql.append(" and instr(innercode,?) > 0");
			sp.addParam(vo.getCorpcode());
		}
		String total = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor()).toString();
		return Integer.valueOf(total);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CorpVO> queryChannel(ChInvoiceVO vo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select pk_corp,unitname,innercode,vprovince from bd_account account");
		sql.append(" where nvl(dr,0) = 0 and nvl(isaccountcorp,'N') = 'Y' ");
		sql.append(" and nvl(ischannel,'N') = 'Y' ");
		sql.append(" and "+QueryUtil.getWhereSql());
		if (vo.getDr() != null && vo.getDr() >= 0) {// 给区域划分（省市过滤）用的
			sql.append(" and vprovince=? ");
			sp.addParam(vo.getDr());
			if (!StringUtil.isEmpty(vo.getVmome())) {
				String[] split = vo.getVmome().split(",");
				sql.append(" and pk_corp not in (");
				sql.append(SqlUtil.buildSqlConditionForIn(split));
				sql.append(" )");
			}
		} else if (vo.getDr() != null && vo.getDr() < 0 && vo.getDr() != -1) {// 增加权限的加盟商参照 -2（渠道） -3（培训） -4（运营）
			String condition = pubser.getPowerSql(vo.getEmail(), vo.getDr()==-5 ? 2 :-vo.getDr()-1);
			if (condition != null && !condition.equals("alldata")) {
				sql.append(condition);
			} else if (condition == null) {
				return null;
			}
			if (vo.getDr() == -5) {// 数据运营管理，4个报表
				sql.append(" and pk_corp not in (");
				sql.append("       (SELECT f.pk_corp  \n");
				sql.append("          FROM ynt_franchisee f  \n");
				sql.append("         WHERE nvl(dr, 0) = 0  \n");
				sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
				sql.append(" )");
			}
		}
		sql.append(" order by innercode ");
		List<CorpVO> list = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(CorpVO.class));
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname" }, list, 1);
			List<CorpVO> rList = new ArrayList<>();
			if (!StringUtil.isEmpty(vo.getCorpcode())) {
				for (CorpVO cvo : list) {
					if (cvo.getUnitname().contains(vo.getCorpcode()) || cvo.getInnercode().contains(vo.getCorpcode())) {
						rList.add(cvo);
					}
				}
				return rList;
			}
		}
		return list;
	}

	@Override
	public List<ChInvoiceVO> onBilling(String[] pk_invoices, String userid, String invtime) throws DZFWarpException {
		if (pk_invoices == null || pk_invoices.length == 0) {
			throw new BusinessException("请选择发票！");
		}
		List<ChInvoiceVO> lists = new ArrayList<ChInvoiceVO>();
		List<ChInvoiceVO> listError = new ArrayList<ChInvoiceVO>();
		HashMap<String, DZFDouble> mapUse = queryUsedMny();
		for (String pk : pk_invoices) {
			String uuid = UUID.randomUUID().toString();
			ChInvoiceVO vo = new ChInvoiceVO();
			try {
				LockUtil.getInstance().tryLockKey(tablename, pk, uuid, 60);
				vo = queryByPk(pk);

				DZFDouble umny = CommonUtil.getDZFDouble(mapUse.get(vo.getPk_corp()));// 累计合同扣款金额
				DZFDouble invmny = queryInvoiceMny(vo.getPk_corp());// 累计合同开票金额
				if (vo.getInvstatus() != 1) {
					vo.setMsg("要确认开票的单据不是待开票状态");
					listError.add(vo);
					continue;
				}
				DZFDouble invprice = new DZFDouble(vo.getInvprice());
				if (invprice.compareTo(umny.sub(invmny)) > 0) {
					StringBuffer msg = new StringBuffer();
					msg.append("你本次要确认开票的金额").append(invprice.setScale(2, DZFDouble.ROUND_HALF_UP)).append("元大于可开票金额")
							.append(umny.sub(invmny).setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，请确认。");
					vo.setMsg(msg.toString());
					listError.add(vo);
					continue;
				}
				lists.add(vo);
				vo.setInvperson(userid);
				if (vo.getIsourcetype() != null && vo.getIsourcetype() == 1) {
					updateContTicketMny(vo);
				} else if (vo.getIsourcetype() != null && vo.getIsourcetype() == 2) {
					updateBillTicketMny(vo);
				}
				updateInvoice(vo, invtime);

			} catch (Exception e) {
				if (e instanceof BusinessException) {
					vo = queryByPk(pk);
					vo.setMsg(e.getMessage());
					listError.add(vo);
				} else
					throw new WiseRunException(e);
			} finally {
				LockUtil.getInstance().unLock_Key(tablename, pk, uuid);
			}
		}
		return listError;
	}

	@Override
	public boolean hasDigit(String content) throws DZFWarpException {
		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches()) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 开具电子票（本接口已经废弃 2019-03-15 下一版删除）
	 */
	@Override
	public List<ChInvoiceVO> onAutoBill(String[] pk_invoices, UserVO uvo) throws DZFWarpException {

		// if (pk_invoices == null || pk_invoices.length == 0) {
		// throw new BusinessException("请选择数据");
		// }
		//
		// if (uvo.getUser_name().length() > 8) {
		// throw new BusinessException("操作用户名称长度不能大于8");
		// } else if (hasDigit(uvo.getUser_name())) {
		// throw new BusinessException("操作用户名称不能包含数字");
		// }

		checkBeforeAutoBill(pk_invoices, uvo);

		ChInvoiceVO[] cvos = queryByPks(pk_invoices);
		if (cvos == null || cvos.length == 0) {
			throw new BusinessException("请选择数据。");
		}

		List<ChInvoiceVO> errlist = new ArrayList<ChInvoiceVO>();
		HashMap<String, DZFDouble> useMap = queryUsedMny();

		List<String> pklist = new ArrayList<String>();// 订单主键
		Map<String, List<GoodsBillBVO>> bmap = null;
		for (ChInvoiceVO vo : cvos) {
			if (vo.getInvtype() != 2) {
				throw new BusinessException("您好！只有申请开具电子发票的开票申请才可提交电子发票自动开票接口，请知悉并重新选择数据。");
			}
			if (!StringUtil.isEmpty(vo.getPk_source())) {
				pklist.add(vo.getPk_source());
			}
		}
		if (pklist != null && pklist.size() > 0) {
			bmap = queryGoodsBill(pklist);
		}

		for (ChInvoiceVO vo : cvos) {
			String uuid = UUID.randomUUID().toString();
			try {
				LockUtil.getInstance().tryLockKey(vo.getTableName(), vo.getPk_invoice(), uuid, 60);

				if (vo.getInvstatus() != 1 && vo.getInvstatus() != 3) {
					vo.setMsg("要确认开票的单据不是待开票状态");
					errlist.add(vo);
					continue;
				}

				if (vo.getIsourcetype() != null && vo.getIsourcetype() == 1) {// 1：合同扣款开票；
					DZFDouble umny = CommonUtil.getDZFDouble(useMap.get(vo.getPk_corp()));
					DZFDouble invmny = queryInvoiceMny(vo.getPk_corp());
					DZFDouble invprice = new DZFDouble(vo.getInvprice());
					if (invprice.compareTo(umny.sub(invmny)) > 0) {
						StringBuffer msg = new StringBuffer();
						msg.append("你本次要确认开票的金额");
						msg.append(invprice.setScale(2, DZFDouble.ROUND_HALF_UP));
						msg.append("元大于可开票金额");
						msg.append(umny.sub(invmny).setScale(2, DZFDouble.ROUND_HALF_UP));
						msg.append("元，请确认。");
						vo.setMsg(msg.toString());
						errlist.add(vo);
						continue;
					}
				}

				vo.setInvperson(uvo.getCuserid());
				PiaoTongResVO resvo = savePiaoTong(vo, uvo, bmap);
				if (resvo == null) {
					vo.setMsg("票通未返回接收数据结果。");
					errlist.add(vo);
				} else if (!IPiaoTongConstant.SUCCESS.equals(resvo.getCode())) {
					vo.setMsg(resvo.getMsg());
					errlist.add(vo);
				}
			} catch (Exception e) {
				if (e instanceof BusinessException) {
					vo.setMsg(e.getMessage());
					errlist.add(vo);
				} else
					throw new WiseRunException(e);
			} finally {
				LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getPk_invoice(), uuid);
			}
		}
		return errlist;
	}

	/**
	 * 开具电子发票前校验
	 * 
	 * @param pk_invoices
	 * @param uvo
	 * @throws DZFWarpException
	 */
	private void checkBeforeAutoBill(String[] pk_invoices, UserVO uvo) throws DZFWarpException {
		if (pk_invoices == null || pk_invoices.length == 0) {
			throw new BusinessException("请选择数据");
		}

		if (uvo.getUser_name().length() > 8) {
			throw new BusinessException("操作用户名称长度不能大于8");
		} else if (hasDigit(uvo.getUser_name())) {
			throw new BusinessException("操作用户名称不能包含数字");
		}
	}

	private ChInvoiceVO[] queryByPks(String[] pk_invoices) {
		String condition = SqlUtil.buildSqlForIn("pk_invoice", pk_invoices);
		StringBuffer sql = new StringBuffer();
		sql.append(" invstatus in (1,2,3)");
		sql.append("and ").append(condition);
		return (ChInvoiceVO[]) singleObjectBO.queryByCondition(ChInvoiceVO.class, sql.toString(), null);
	}

	/**
	 * 调用票通接口，开具电子发票
	 * 
	 * @param cvo
	 * @param uvo
	 * @param bmap
	 * @return
	 * @throws DZFWarpException
	 */
	private PiaoTongResVO savePiaoTong(ChInvoiceVO cvo, UserVO uvo, Map<String, List<GoodsBillBVO>> bmap)
			throws DZFWarpException {
		PiaoTongInvVO hvo = getHeadInfo(cvo, uvo);

		// 开票项目信息
		List<PiaoTongInvBVO> itemList = new ArrayList<>();

		// 发票来源类型 1：合同扣款开票； 2：商品扣款开票；
		if (cvo.getIsourcetype() != null && cvo.getIsourcetype() == 1) {
			itemList = getContItem(cvo);
		} else if (cvo.getIsourcetype() != null && cvo.getIsourcetype() == 2) {
			itemList = getBillItemList(cvo, bmap);
		}
		// 电子发票详情
		hvo.setItemList(itemList);

		PiaoTongBill bill = new PiaoTongBill();
		PiaoTongResVO resvo = bill.sendBill(hvo);
		updateSendBack(resvo, cvo);
		return resvo;
	}

	/**
	 * 获取开票主信息
	 * 
	 * @param cvo
	 * @param uvo
	 * @return
	 * @throws DZFWarpException
	 */
	private PiaoTongInvVO getHeadInfo(ChInvoiceVO cvo, UserVO uvo) throws DZFWarpException {
		PiaoTongInvVO hvo = new PiaoTongInvVO();
		// 销方信息
		// hvo.setTaxpayerNum("91110108397823696Y");//大账房
		// hvo.setSellerTaxpayerNum("91110108397823696Y");
		// hvo.setSellerEnterpriseName("北京大账房信息技术有限公司");
		// 测试
		// hvo.setTaxpayerNum("110101201702071");//
		// hvo.setSellerTaxpayerNum("110101201702071");
		hvo.setSellerEnterpriseName("北京大账房信息技术有限公司");
		hvo.setSellerAddress("北京海淀区万泉庄路15号5层501");
		hvo.setSellerTel("010-82552270");
		hvo.setSellerBankName("建行西直门北大街支行");
		hvo.setSellerBankAccount("11001174900053000702");
		hvo.setCasherName(uvo.getUser_name());
		hvo.setDrawerName(uvo.getUser_name());

		// 购买方信息
		hvo.setBuyerName(cvo.getCorpname());
		hvo.setBuyerTaxpayerNum(cvo.getTaxnum());

		// 电子发票，购买方地址、电话、开户行及账号信息传空
		// hvo.setBuyerAddress(cvo.getCorpaddr());
		// hvo.setBuyerBankName(cvo.getBankname());
		// hvo.setBuyerBankAccount(cvo.getBankcode());
		// hvo.setBuyerTel(cvo.getInvphone());

		hvo.setTakerEmail(cvo.getEmail());
		// hvo.setTakerEmail("gejingwei@dazhangfang.com");
		hvo.setTakerName(cvo.getRusername());
		hvo.setTakerTel(cvo.getInvphone());
		return hvo;
	}

	/**
	 * 开票项目信息（合同扣款开票）
	 * 
	 * @param cvo
	 * @return
	 * @throws DZFWarpException
	 */
	private List<PiaoTongInvBVO> getContItem(ChInvoiceVO cvo) throws DZFWarpException {
		List<PiaoTongInvBVO> itemList = new ArrayList<>();
		PiaoTongInvBVO bvo = new PiaoTongInvBVO();
		bvo.setGoodsName("技术服务费");
		// 对应税收分类编码---信息技术服务
		bvo.setTaxClassificationCode("3040203000000000000");
		bvo.setQuantity("1.00");// 数量
		bvo.setIncludeTaxFlag("1");// 含税标志
		bvo.setUnitPrice(cvo.getInvprice().toString());// 单价
		bvo.setInvoiceAmount(cvo.getInvprice().setScale(2, DZFDouble.ROUND_HALF_UP).toString());// 金额
		bvo.setTaxRateValue("0.06");// 税率
		itemList.add(bvo);
		return itemList;
	}

	/**
	 * 开票项目信息（商品订单开票-全扣预付款）
	 * 
	 * @param cvo
	 * @return
	 * @throws DZFWarpException
	 */
	private List<PiaoTongInvBVO> getBillItemByYf(ChInvoiceVO cvo) throws DZFWarpException {
		List<PiaoTongInvBVO> itlist = new ArrayList<PiaoTongInvBVO>();
		List<GoodsBillBVO> list = queryGoodsBillByYf(cvo);
		if (list != null && list.size() > 0) {
			PiaoTongInvBVO bvo = null;
			StringBuffer spectype = null;
			for (GoodsBillBVO blvo : list) {
				bvo = new PiaoTongInvBVO();
				bvo.setGoodsName(blvo.getVgoodsname());
				if (!StringUtil.isEmpty(blvo.getVtaxclasscode())) {
					// 对应税收分类编码
					bvo.setTaxClassificationCode(blvo.getVtaxclasscode());
				} else {
					throw new BusinessException("商品名称【" + blvo.getVgoodsname() + "】税收分类编码不能为空！");
				}
				bvo.setQuantity(String.valueOf(blvo.getAmount()) + ".00");// 数量
				bvo.setIncludeTaxFlag("1");// 含税标志 0：不含税，1：含税。
				bvo.setUnitPrice(blvo.getNprice().setScale(2, DZFDouble.ROUND_HALF_UP).toString());// 单价
				bvo.setInvoiceAmount(blvo.getNtotalmny().setScale(2, DZFDouble.ROUND_HALF_UP).toString());// 金额
				bvo.setTaxRateValue("0.16");// 税率
				spectype = new StringBuffer();
				if (!StringUtil.isEmpty(blvo.getInvspec())) {
					spectype.append(blvo.getInvspec());
				}
				if (!StringUtil.isEmpty(blvo.getInvtype())) {
					spectype.append(blvo.getInvtype());
				}
				if (spectype != null && spectype.length() > 0) {
					bvo.setSpecificationModel(spectype.toString());// 规格型号
				}
				bvo.setMeteringUnit(blvo.getVmeasname());// 单位
				itlist.add(bvo);
			}
		}
		return itlist;
	}

	/**
	 * 查询订单明细信息（订单全扣预付款）
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<GoodsBillBVO> queryGoodsBillByYf(ChInvoiceVO cvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT b.*, s.vtaxclasscode  \n");
		sql.append("  FROM cn_goodsbill_b b  \n");
		sql.append("  LEFT JOIN cn_goods s ON b.pk_goods = s.pk_goods  \n");
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
		sql.append("   AND nvl(s.dr, 0) = 0  \n");
		sql.append("   AND b.pk_corp = ?  \n");
		sql.append("   AND b.pk_goodsbill = ? \n");
		spm.addParam(cvo.getPk_corp());
		spm.addParam(cvo.getPk_source());
		return (List<GoodsBillBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(GoodsBillBVO.class));
	}

	/**
	 * 开票项目信息（商品订单开票-既扣预付款，又扣返点）
	 * 
	 * @param cvo
	 * @return
	 * @throws DZFWarpException
	 */
	private List<PiaoTongInvBVO> getBillItemByAll(ChInvoiceVO cvo) throws DZFWarpException {
		List<PiaoTongInvBVO> itlist = new ArrayList<PiaoTongInvBVO>();
		List<ChInvoiceBVO> list = queryGoodsBillByAll(cvo);
		if (list != null && list.size() > 0) {
			PiaoTongInvBVO bvo = null;
			for (ChInvoiceBVO blvo : list) {
				bvo = new PiaoTongInvBVO();
				bvo.setGoodsName(blvo.getBspmc());
				if (!StringUtil.isEmpty(blvo.getVtaxclasscode())) {
					// 对应税收分类编码
					bvo.setTaxClassificationCode(blvo.getVtaxclasscode());
				} else {
					throw new BusinessException("商品名称【" + blvo.getBspmc() + "】税收分类编码不能为空！");
				}
				bvo.setQuantity(String.valueOf(blvo.getBnum()) + ".00");// 数量
				bvo.setIncludeTaxFlag("0");// 含税标志 0：不含税，1：含税。
				bvo.setUnitPrice(blvo.getBprice().setScale(4, DZFDouble.ROUND_HALF_UP).toString());// 单价（不含税）
				bvo.setInvoiceAmount(blvo.getBhjje().setScale(2, DZFDouble.ROUND_HALF_UP).toString());// 金额（不含税）
				bvo.setTaxRateValue("0.16");// 税率
				if (!StringUtil.isEmpty(blvo.getInvspec())) {
					bvo.setSpecificationModel(blvo.getInvspec());// 规格型号
				}
				bvo.setMeteringUnit(blvo.getMeasurename());// 单位
				itlist.add(bvo);
			}
		}
		return itlist;
	}

	/**
	 * 查询订单明细信息（订单既扣预付款，又扣返点）
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<ChInvoiceBVO> queryGoodsBillByAll(ChInvoiceVO cvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT b.*, s.vtaxclasscode  \n");
		sql.append("  FROM cn_invoice_b b  \n");
		sql.append("  LEFT JOIN cn_goods s ON b.pk_goods = s.pk_goods  \n");
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
		sql.append("   AND nvl(s.dr, 0) = 0  \n");
		sql.append("   AND b.pk_corp = ?  \n");
		sql.append("   AND b.pk_goodsbill = ? \n");
		spm.addParam(cvo.getPk_corp());
		spm.addParam(cvo.getPk_source());
		return (List<ChInvoiceBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChInvoiceBVO.class));
	}

	/**
	 * 开票项目信息（商品购买开票）
	 * 
	 * @param cvo
	 * @return
	 * @throws DZFWarpException
	 */
	private List<PiaoTongInvBVO> getBillItemList(ChInvoiceVO cvo, Map<String, List<GoodsBillBVO>> bmap)
			throws DZFWarpException {
		List<PiaoTongInvBVO> itlist = new ArrayList<PiaoTongInvBVO>();
		List<GoodsBillBVO> list = bmap.get(cvo.getPk_source());
		if (list != null && list.size() > 0) {
			PiaoTongInvBVO bvo = null;
			StringBuffer spectype = null;
			for (GoodsBillBVO blvo : list) {
				bvo = new PiaoTongInvBVO();
				bvo.setGoodsName(blvo.getVgoodsname());
				if (!StringUtil.isEmpty(blvo.getVtaxclasscode())) {
					// 对应税收分类编码
					bvo.setTaxClassificationCode(blvo.getVtaxclasscode());
				} else {
					throw new BusinessException("商品名称【" + blvo.getVgoodsname() + "】税收分类编码异常！");
				}
				bvo.setQuantity(String.valueOf(blvo.getAmount()) + ".00");// 数量
				bvo.setIncludeTaxFlag("1");// 含税标志 0：不含税，1：含税。
				bvo.setUnitPrice(blvo.getNprice().setScale(2, DZFDouble.ROUND_HALF_UP).toString());// 单价
				bvo.setInvoiceAmount(blvo.getNtotalmny().setScale(2, DZFDouble.ROUND_HALF_UP).toString());// 金额
				bvo.setTaxRateValue("0.16");// 税率
				spectype = new StringBuffer();
				if (!StringUtil.isEmpty(blvo.getInvspec())) {
					spectype.append(blvo.getInvspec());
				}
				if (!StringUtil.isEmpty(blvo.getInvtype())) {
					spectype.append(blvo.getInvtype());
				}
				if (spectype != null && spectype.length() > 0) {
					bvo.setSpecificationModel(spectype.toString());// 规格型号
				}
				bvo.setMeteringUnit(blvo.getVmeasname());// 单位
				itlist.add(bvo);
			}
		}

		return itlist;
	}

	/**
	 * 查询订单明细信息
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, List<GoodsBillBVO>> queryGoodsBill(List<String> pklist) throws DZFWarpException {
		Map<String, List<GoodsBillBVO>> retmap = new HashMap<String, List<GoodsBillBVO>>();
		if (pklist != null && pklist.size() > 0) {
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			sql.append("SELECT b.*, s.vtaxclasscode  \n");
			sql.append("  FROM cn_goodsbill_b b  \n");
			sql.append("  LEFT JOIN cn_goods s ON b.pk_goods = s.pk_goods  \n");
			sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
			sql.append("   AND nvl(s.dr, 0) = 0  \n");
			String where = SqlUtil.buildSqlForIn("b.pk_goodsbill", pklist.toArray(new String[0]));
			sql.append(" AND ").append(where);
			List<GoodsBillBVO> list = (List<GoodsBillBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
					new BeanListProcessor(GoodsBillBVO.class));
			if (list != null && list.size() > 0) {
				List<GoodsBillBVO> newlist = null;
				List<GoodsBillBVO> oldlist = null;
				String pk_goodsbill = "";
				for (GoodsBillBVO bvo : list) {
					pk_goodsbill = bvo.getPk_goodsbill();
					if (!retmap.containsKey(pk_goodsbill)) {
						newlist = new ArrayList<GoodsBillBVO>();
						newlist.add(bvo);
						retmap.put(pk_goodsbill, newlist);
					} else {
						oldlist = retmap.get(pk_goodsbill);
						oldlist.add(bvo);
						retmap.put(pk_goodsbill, oldlist);
					}
				}
			}
		}

		return retmap;
	}

	/**
	 * 调用票通接口开票成功后，回写发票信息
	 * 
	 * @param resvo
	 * @param cvo
	 */
	private void updateSendBack(PiaoTongResVO resvo, ChInvoiceVO cvo) {
		if (resvo != null && IPiaoTongConstant.SUCCESS.equals(resvo.getCode())) {
			updatePtInvoice(resvo, cvo);
			if (cvo.getIsourcetype() != null && cvo.getIsourcetype() == 1) {
				updateContTicketMny(cvo);
			} else if (cvo.getIsourcetype() != null && cvo.getIsourcetype() == 2) {
				updateBillTicketMny(cvo);
			}
		} else if (resvo == null || StringUtil.isEmpty(resvo.getCode()) || StringUtil.isEmpty(resvo.getMsg())) {
			updatePtNotInv(resvo, cvo);
		} else if (!IPiaoTongConstant.SUCCESS.equals(resvo.getCode())) {
			updatePtNotInv(resvo, cvo);
		}
	}

	/**
	 * 开票成功：自动开票回写
	 * 
	 * @author gejw
	 * @time 下午2:46:11
	 * @param resvo
	 */
	private void updatePtInvoice(PiaoTongResVO resvo, ChInvoiceVO cvo) {
		PiaoTongResBVO bvo = resvo.getBvo();
		cvo.setReqserialno(bvo.getInvoiceReqSerialNo());
		cvo.setQrcodepath(Base64.decode(bvo.getQrCodePath()));
		DZFDate time = new DZFDate();
		cvo.setInvtime(time.toString());
		cvo.setInvstatus(2);
		cvo.setBillway(1);
		singleObjectBO.update(cvo,
				new String[] { "reqserialno", "qrcodepath", "invtime", "invperson", "invstatus", "billway" });
	}

	/**
	 * 开票失败：自动开票回写
	 * 
	 * @author gejw
	 * @time 上午9:56:08
	 * @param cvo
	 */
	private void updatePtNotInv(PiaoTongResVO resvo, ChInvoiceVO cvo) {
		cvo.setErrcode(resvo.getCode());
		cvo.setInvstatus(3);
		cvo.setBillway(1);
		singleObjectBO.update(cvo, new String[] { "errcode", "invstatus", "billway" });
	}

	/**
	 * 查询累计合同扣款金额
	 * 
	 * @param pk_invoices
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, DZFDouble> queryUsedMny() throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select a.pk_corp,  \n");
		sql.append("       sum(nvl(d.nusedmny, 0)) as debitconmny \n");// 累计合同扣款金额
		sql.append("  from bd_account a  \n");
		sql.append("  left join cn_detail d on a.pk_corp = d.pk_corp  \n");
		sql.append("                            and d.iopertype = 2  \n");
		sql.append("                            and d.ipaytype = 2  \n");
		sql.append("                            and nvl(d.dr, 0) = 0  \n");
		sql.append("                            and d.doperatedate <= ?  \n");
		sp.addParam(new DZFDate());
		sql.append(" where nvl(a.dr,0) = 0  \n");
		sql.append("   and nvl(d.dr,0) = 0  \n");
		sql.append("   and a.ischannel = 'Y'  \n");
		sql.append(" group by a.pk_corp  \n");

		List<BillingInvoiceVO> list = (List<BillingInvoiceVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(BillingInvoiceVO.class));
		HashMap<String, DZFDouble> map = new HashMap<>();
		if (list != null && list.size() > 0) {
			for (BillingInvoiceVO bvo : list) {
				map.put(bvo.getPk_corp(), CommonUtil.getDZFDouble(bvo.getDebitconmny()));
			}
		}
		return map;
	}

	/**
	 * 查询累计合同开票金额
	 * 
	 * @param vo
	 */
	private DZFDouble queryInvoiceMny(String pk_corp) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select sum(nvl(invprice, 0)) as billtotalmny  \n");// 累计合同开票金额
		sql.append("  from cn_invoice  \n");
		sql.append(" where nvl(dr, 0) = 0  \n");
		sql.append("   and invstatus = 2 \n");
		sql.append("   and isourcetype = 1 \n");// 发票来源类型 1：合同扣款开票； 2：商品扣款开票；
		sql.append("   and apptime <= ?  \n");
		spm.addParam(new DZFDate());
		sql.append("   and pk_corp = ?  \n");
		spm.addParam(pk_corp);
		sql.append(" group by pk_corp ");
		Object obj = singleObjectBO.executeQuery(sql.toString(), spm, new ColumnProcessor());
		return obj == null ? DZFDouble.ZERO_DBL : new DZFDouble(obj.toString());
	}

	/**
	 * 根据主键查询
	 * 
	 * @param pk_invoice
	 * @return
	 */
	private ChInvoiceVO queryByPk(String pk_invoice) {
		return (ChInvoiceVO) singleObjectBO.queryByPrimaryKey(ChInvoiceVO.class, pk_invoice);
	}

	/**
	 * 更新（合同扣款开票金额）开票金额
	 */
	private void updateContTicketMny(ChInvoiceVO vo) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getInvprice());
		sp.addParam(vo.getPk_corp());
		if (vo.getIpaytype() == 0) {
			sp.addParam(2);
		}
		sql.append("update cn_balance  \n");
		sql.append("   set nticketmny = nvl(nticketmny, 0) + ?  \n");
		sql.append(" where nvl(dr, 0) = 0  \n");
		sql.append("   and pk_corp = ?  \n");
		sql.append("   and ipaytype = ?  \n");
		singleObjectBO.executeUpdate(sql.toString(), sp);
	}

	/**
	 * 更新（商品购买开票金额）开票金额
	 */
	private void updateBillTicketMny(ChInvoiceVO vo) {
		GoodsBillVO hvo = (GoodsBillVO) singleObjectBO.queryByPrimaryKey(GoodsBillVO.class, vo.getPk_source());
		if (hvo != null) {
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			// 1、预付款扣款
			if (CommonUtil.getDZFDouble(hvo.getNdeductmny()).compareTo(DZFDouble.ZERO_DBL) > 0) {
				sql.append("UPDATE cn_balance  \n");
				sql.append("   SET nticketbuymny = nvl(nticketbuymny, 0) + ?  \n");
				spm.addParam(hvo.getNdeductmny());
				sql.append(" WHERE nvl(dr, 0) = 0  \n");
				sql.append("   AND pk_corp = ?  \n");
				sql.append("   AND ipaytype = ?  \n");
				spm.addParam(hvo.getPk_corp());
				spm.addParam(IStatusConstant.IPAYTYPE_2);// 预付款
				int res = singleObjectBO.executeUpdate(sql.toString(), spm);
				if (res != 1) {
					throw new BusinessException("商品购买开票金额回写错误");
				}
			}
			// //2、返点扣款
			// if(CommonUtil.getDZFDouble(hvo.getNdedrebamny()).compareTo(DZFDouble.ZERO_DBL)
			// > 0){
			// sql = new StringBuffer();
			// spm = new SQLParameter();
			// sql.append("UPDATE cn_balance \n") ;
			// sql.append(" SET nticketbuymny = nvl(nticketbuymny, 0) + ? \n") ;
			// spm.addParam(hvo.getNdedrebamny());
			// sql.append(" WHERE nvl(dr, 0) = 0 \n") ;
			// sql.append(" AND pk_corp = ? \n") ;
			// sql.append(" AND ipaytype = ? \n");
			// spm.addParam(hvo.getPk_corp());
			// spm.addParam(IStatusConstant.IPAYTYPE_3);//返点
			// int res = singleObjectBO.executeUpdate(sql.toString(), spm);
			// if(res != 1){
			// throw new BusinessException("商品购买开票金额回写错误");
			// }
			// }
		} else {
			throw new BusinessException("商品购买开票金额回写错误");
		}

		// spm.addParam(vo.getInvprice());
		// spm.addParam(vo.getPk_corp());
		// if (vo.getIpaytype() == 0) {
		// spm.addParam(2);
		// }
		// sql.append("update cn_balance set nticketmny = nvl(nticketmny,0) + ?
		// ");
		// sql.append("where nvl(dr,0)=0 and pk_corp = ? and ipaytype = ?");
		// singleObjectBO.executeUpdate(sql.toString(), spm);
	}

	/**
	 * 更新发票
	 */
	private void updateInvoice(ChInvoiceVO[] vos) {
		DZFDate time = new DZFDate();
		for (ChInvoiceVO vo : vos) {
			vo.setInvtime(time.toString());
			vo.setInvstatus(2);
		}
		singleObjectBO.updateAry(vos, new String[] { "invtime", "invperson", "invstatus" });
	}

	private void updateInvoice(ChInvoiceVO vo, String invtime) {
		if (StringUtil.isEmpty(invtime)) {
			invtime = new DZFDate().toString();
		}
		vo.setInvtime(invtime);
		vo.setInvstatus(2);
		vo.setBillway(2);
		singleObjectBO.update(vo, new String[] { "invtime", "invperson", "invstatus", "billway" });
	}

	@Override
	public void delete(ChInvoiceVO vo) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(vo.getTableName(), vo.getPk_invoice(), uuid, 60);
			ChInvoiceVO chvo = (ChInvoiceVO) singleObjectBO.queryByPrimaryKey(ChInvoiceVO.class, vo.getPk_invoice());
			if (chvo != null) {
				if (chvo.getInvstatus() == 2) {
					throw new BusinessException("发票状态为【已开票】，不能删除！");
				}
				if (chvo.getIsourcetype() != null && chvo.getIsourcetype() == 2) {// 商品扣款开票
					// 如果订单扣款为预付款扣款，则只需更新订单开票状态
					// 如果订单扣款为预付款扣款和订单扣款，则需要删除发票相关信息后，更新订单开发状态
					// 数据类型（1：商品扣款全扣预付款；2：商品扣款扣预付款和返点）
					if (chvo.getIdatatype() == 2) {
						deleteOrderInvoice(chvo);
					}
					updateGoodsBillStatus(chvo);
				}
				singleObjectBO.deleteObject(chvo);
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getPk_invoice(), uuid);
		}
	}

	/**
	 * 删除订单发票信息
	 * 
	 * @param vo
	 * @throws DZFWarpException
	 */
	private void deleteOrderInvoice(ChInvoiceVO vo) throws DZFWarpException {
		String sql = " DELETE FROM cn_invoice_b WHERE nvl(dr,0) = 0 AND pk_invoice = ?";
		SQLParameter spm = new SQLParameter();
		spm.addParam(vo.getPk_invoice());
		singleObjectBO.executeUpdate(sql, spm);
	}

	/**
	 * 更新订单状态
	 * 
	 * @param ivo
	 * @throws DZFWarpException
	 */
	private void updateGoodsBillStatus(ChInvoiceVO ivo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("UPDATE cn_goodsbill  \n");
		sql.append("   SET vtistatus = 1  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_goodsbill = ?  \n");
		spm.addParam(ivo.getPk_source());
		int res = singleObjectBO.executeUpdate(sql.toString(), spm);
		if (res != 1) {
			throw new BusinessException("订单状态更新错误");
		}
	}

	@Override
	public ChInvoiceVO queryTotalPrice(String pk_corp, int ipaytype, String invprice) throws DZFWarpException {
		ChInvoiceVO returnVo = new ChInvoiceVO();
		DZFDouble addPrice = queryAddPrice(pk_corp, ipaytype, invprice);
		DZFDouble ticketPrice = queryTicketPrice(pk_corp, ipaytype);
		DZFDouble nticketmny = ticketPrice.sub(addPrice);

		returnVo.setNticketmny(String.valueOf(nticketmny));
		return returnVo;
	}

	/**
	 * 查询已添加的开票金额总额
	 */
	private DZFDouble queryAddPrice(String pk_corp, int ipaytype, String invprice) {

		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select nvl(sum(nvl(invprice, 0)), 0) price  \n");
		sql.append("  from cn_invoice  \n");
		sql.append(" where nvl(dr, 0) = 0  \n");
		sql.append("   and pk_corp = ?  \n");
		//发票状态  0：待提交 ；1：待开票；2：已开票；3：开票失败；9：已换票；
		sql.append("   and invstatus NOT IN ( 0, 2)  \n");
		sql.append("   and ipaytype = ?  \n");
		sp.addParam(pk_corp);
		sp.addParam(ipaytype);
		sql.append("   and isourcetype = 1 \n");// 发票来源类型 1：合同扣款开票；
		String price = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("price")).toString();
		DZFDouble nprice = new DZFDouble(price);
		if (!StringUtil.isEmpty(invprice)) {
			nprice = nprice.sub(new DZFDouble(invprice));
		}
		return nprice;
	}

	@SuppressWarnings("unchecked")
	private DZFDouble queryTicketPrice(String pk_corp, int ipaytype) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		// 1、查询合同扣款金额
		sql.append("SELECT SUM(nvl(nusedmny, 0)) AS nusedmny  \n");// 合同扣款金额
		sql.append("  FROM cn_detail  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_corp = ?  \n");
		spm.addParam(pk_corp);
		sql.append("   AND ipaytype = 2  \n");
		sql.append("   AND iopertype = 2  \n");
		ArrayList<Object> res = (ArrayList<Object>) singleObjectBO.executeQuery(sql.toString(), spm,
				new ArrayListProcessor());
		DZFDouble usedmny = DZFDouble.ZERO_DBL;
		if (res != null && !res.isEmpty()) {
			Object[] obj = (Object[]) res.get(0);
			usedmny = CommonUtil.getDZFDouble(obj[0]);
		}

		// 2、查询合同扣款开票金额
		sql = new StringBuffer();
		spm = new SQLParameter();
		sql.append("select sum(nvl(nticketmny, 0)) as nticketmny \n");// 合同扣款开票金额
		sql.append("  from cn_balance  \n");
		sql.append(" where nvl(dr, 0) = 0  \n");
		sql.append("   and pk_corp = ?  \n");
		sql.append("   and ipaytype = ?  \n");
		spm.addParam(pk_corp);
		if (ipaytype == 0) {
			ipaytype = 2;
		}
		spm.addParam(ipaytype);
		res = (ArrayList<Object>) singleObjectBO.executeQuery(sql.toString(), spm, new ArrayListProcessor());
		DZFDouble ticketmny = DZFDouble.ZERO_DBL;
		if (res != null && !res.isEmpty()) {
			Object[] obj = (Object[]) res.get(0);
			ticketmny = CommonUtil.getDZFDouble(obj[0]);
		}

		return SafeCompute.sub(usedmny, ticketmny);
	}

	@Override
	public void save(ChInvoiceVO vo) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(vo.getTableName(), vo.getPk_invoice(), uuid, 60);
			ChInvoiceVO ovo = (ChInvoiceVO) singleObjectBO.queryByPrimaryKey(ChInvoiceVO.class, vo.getPk_invoice());
			if (ovo == null) {
				throw new BusinessException("数据已被删除，请刷新重新操作。");
			}
			if (ovo.getUpdatets() != null) {
				if (vo.getUpdatets() != null && !vo.getUpdatets().equals(ovo.getUpdatets())) {
					throw new BusinessException("数据已发生变化，请刷新后操作");
				} else if (vo.getUpdatets() == null) {
					throw new BusinessException("数据已发生变化，请刷新后操作");
				}
			}
			if (StringUtil.isEmpty(vo.getPk_corp())) {
				vo.setPk_corp(ovo.getPk_corp());
			}
			if (hasDigit(vo.getRusername())) {
				throw new BusinessException("收票人不能包含数字。");
			}
			if (ovo.getInvstatus() == 2) {
				throw new BusinessException("已开票，不允许修改。");
			}
			String[] fieldNames = new String[] { "taxnum", "invprice", "invtype", "corpaddr", "invphone", "bankname",
					"bankcode", "email", "vmome", "rusername" };
			if (ovo.getInvstatus() == 3) {
				vo.setInvstatus(1);
				fieldNames = new String[] { "taxnum", "invprice", "invtype", "corpaddr", "invphone", "bankname",
						"bankcode", "email", "vmome", "rusername", "invstatus" };
			}
			checkInvPrice(vo);
			singleObjectBO.update(vo, fieldNames);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getPk_invoice(), uuid);
		}
	}

	/**
	 * 校验开票金额（>0 && <=可开票金额）
	 */
	private void checkInvPrice(ChInvoiceVO vo) {
		if (new DZFDouble(vo.getInvprice()).sub(new DZFDouble(0)).doubleValue() <= 0) {
			throw new BusinessException("开票金额小于0！");
		}
		DZFDouble addPrice = queryAddPrice(vo.getPk_corp(), vo.getIpaytype(), vo.getTempprice());
		DZFDouble ticketPrice = queryTicketPrice(vo.getPk_corp(), vo.getIpaytype());
		DZFDouble invPrice = new DZFDouble(vo.getInvprice());
		DZFDouble nticketmny = ticketPrice.sub(addPrice).sub(invPrice);
		if (nticketmny.doubleValue() < 0) {
			throw new BusinessException("开票金额不可以大于可开票金额，请重新填写");
		}
	}

	@Override
	public InvInfoResBVO[] queryInvRepertoryInfo() throws DZFWarpException {
		QueryInvInfoVO qvo = new QueryInvInfoVO();
		// qvo.setEnterpriseName("北京大账房信息技术有限公司");
		// qvo.setEnterpriseName("电子票测试新1");
		PiaoTongBill bill = new PiaoTongBill();
		InvInfoResBVO[] resvos = bill.queryInvRepertoryInfo(qvo);
		if (resvos != null && resvos.length > 0) {
			for (InvInfoResBVO revo : resvos) {
				if (revo.getInvoiceKindCode().equals(IPiaoTongConstant.INVOICEKINDCODE_01)) {
					revo.setInvoiceKindCode(IPiaoTongConstant.INVOICEKINDNAME_01);
				} else if (revo.getInvoiceKindCode().equals(IPiaoTongConstant.INVOICEKINDCODE_02)) {
					revo.setInvoiceKindCode(IPiaoTongConstant.INVOICEKINDNAME_02);
				} else if (revo.getInvoiceKindCode().equals(IPiaoTongConstant.INVOICEKINDCODE_03)) {
					revo.setInvoiceKindCode(IPiaoTongConstant.INVOICEKINDNAME_03);
				} else if (revo.getInvoiceKindCode().equals(IPiaoTongConstant.INVOICEKINDCODE_04)) {
					revo.setInvoiceKindCode(IPiaoTongConstant.INVOICEKINDNAME_04);
				} else if (revo.getInvoiceKindCode().equals(IPiaoTongConstant.INVOICEKINDCODE_10)) {
					revo.setInvoiceKindCode(IPiaoTongConstant.INVOICEKINDNAME_10);
				} else if (revo.getInvoiceKindCode().equals(IPiaoTongConstant.INVOICEKINDCODE_41)) {
					revo.setInvoiceKindCode(IPiaoTongConstant.INVOICEKINDNAME_41);
				}
			}
		}
		return resvos;
	}

	@Override
	public void onChange(ChInvoiceVO data) throws DZFWarpException {
		ChInvoiceVO cvo = queryByPk(data.getPk_invoice());
		if (cvo != null && cvo.getInvstatus() != 2) {
			throw new BusinessException("只有已开票的单据可以换票。");
		}
		singleObjectBO.update(data, new String[] { "dchangedate", "vchangememo" });
	}

	@Override
	public ChInvoiceVO updateAutoBill(ChInvoiceVO cvo, UserVO uservo, HashMap<String, DZFDouble> useMap)
			throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(cvo.getTableName(), cvo.getPk_invoice(), uuid, 60);
			ChInvoiceVO oldvo = checkBeforeUpdateBill(cvo, useMap);
			oldvo.setInvperson(uservo.getCuserid());// 开票人主键
			// 调用票通接口，开具电子票据
			PiaoTongResVO resvo = savePiaoTongBill(oldvo, uservo);
			if (resvo == null) {
				oldvo.setMsg("票通未返回接收数据结果");
			} else {
				if (resvo.getCode() != null && !IPiaoTongConstant.SUCCESS.equals(resvo.getCode())) {
					oldvo.setMsg(resvo.getMsg());
				}
			}
			return oldvo;
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(cvo.getTableName(), cvo.getPk_invoice(), uuid);
		}

	}

	/**
	 * 调用票通接口开具电子发票
	 * 
	 * @param cvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	private PiaoTongResVO savePiaoTongBill(ChInvoiceVO cvo, UserVO uservo) throws DZFWarpException {
		PiaoTongInvVO postvo = getPostData(cvo, uservo);
		PiaoTongBill bill = new PiaoTongBill();
		PiaoTongResVO resvo = bill.sendBill(postvo);
		updateSendBack(resvo, cvo);
		return resvo;
	}

	/**
	 * 拼装调用票通接口所需信息
	 * 
	 * @param cvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	private PiaoTongInvVO getPostData(ChInvoiceVO cvo, UserVO uservo) throws DZFWarpException {
		// 1、发票抬头信息
		PiaoTongInvVO hvo = getHeadInfo(cvo, uservo);
		// 2、开票项目信息
		List<PiaoTongInvBVO> itemlist = new ArrayList<>();
		// 发票来源类型 1：合同扣款开票； 2：商品扣款开票；
		if (cvo.getIsourcetype() != null && cvo.getIsourcetype() == 1) {
			itemlist = getContItem(cvo);
		} else if (cvo.getIsourcetype() != null && cvo.getIsourcetype() == 2) {
			// 数据类型（1：商品扣款全扣预付款；2：商品扣款扣预付款和返点）
			if (cvo.getIdatatype() != null && cvo.getIdatatype() == 1) {
				itemlist = getBillItemByYf(cvo);
			} else if (cvo.getIdatatype() != null && cvo.getIdatatype() == 2) {
				itemlist = getBillItemByAll(cvo);
			}
		}
		// 电子发票详情
		hvo.setItemList(itemlist);
		return hvo;
	}

	/**
	 * 开票前发票时间戳及状态校验
	 * 
	 * @param cvo
	 */
	private ChInvoiceVO checkBeforeUpdateBill(ChInvoiceVO cvo, HashMap<String, DZFDouble> useMap)
			throws DZFWarpException {
		ChInvoiceVO oldvo = (ChInvoiceVO) singleObjectBO.queryByPrimaryKey(ChInvoiceVO.class, cvo.getPk_invoice());
		if (oldvo == null || (oldvo != null && oldvo.getDr() != null && oldvo.getDr() == 1)) {
			throw new BusinessException("数据发生变化");
		}
		// 1、发票状态校验 0：待提交 ；1：待开票；2：已开票；3：开票失败；
		if (oldvo.getInvstatus() != null && oldvo.getInvstatus() != 1 && oldvo.getInvstatus() != 3) {
			throw new BusinessException("发票状态不是待开票状态");
		}
		// 2、发票类型校验 0: 专用发票、 1:普通发票 、2: 电子普通发票
		if (oldvo.getInvtype() != 2) {
			throw new BusinessException("没有申请开具电子发票");
		}
		// 3、可开票金额校验
		if (oldvo.getIsourcetype() != null && oldvo.getIsourcetype() == 1) {// 1：合同扣款开票；
			DZFDouble umny = CommonUtil.getDZFDouble(useMap.get(oldvo.getPk_corp()));
			DZFDouble invmny = queryInvoiceMny(oldvo.getPk_corp());
			DZFDouble invprice = CommonUtil.getDZFDouble(oldvo.getInvprice());
			if (invprice.compareTo(SafeCompute.sub(umny, invmny)) > 0) {
				StringBuffer msg = new StringBuffer();
				msg.append("开票的金额");
				msg.append(invprice.setScale(2, DZFDouble.ROUND_HALF_UP));
				msg.append("元大于可开票金额");
				msg.append(umny.sub(invmny).setScale(2, DZFDouble.ROUND_HALF_UP));
				msg.append("元");
				throw new BusinessException(msg.toString());
			}
		}
		return oldvo;
	}

	/**
	 * 获取查询条件
	 * 
	 * @param cuserid
	 * @param qrytype
	 *            1：渠道经理；2：培训师；3：渠道运营；
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public String getQrySql(String cuserid, Integer qrytype) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		String[] corps = pubser.getManagerCorp(cuserid, qrytype);
		if (corps != null && corps.length > 0) {
			String where = SqlUtil.buildSqlForIn(" t.pk_corp", corps);
			sql.append(" AND ").append(where);
		} else {
			sql.append(" AND t.pk_corp is null \n");
		}
		return sql.toString();
	}

}
