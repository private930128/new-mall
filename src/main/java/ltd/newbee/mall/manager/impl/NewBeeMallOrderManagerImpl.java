package ltd.newbee.mall.manager.impl;

import com.google.common.collect.Lists;
import ltd.newbee.mall.app.dto.CreateOrderRequest;
import ltd.newbee.mall.common.*;
import ltd.newbee.mall.controller.vo.*;
import ltd.newbee.mall.dao.*;
import ltd.newbee.mall.entity.*;
import ltd.newbee.mall.manager.NewBeeMallOrderManager;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.NumberUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;


@Service
public class NewBeeMallOrderManagerImpl implements NewBeeMallOrderManager {

    @Autowired
    private NewBeeMallOrderMapper newBeeMallOrderMapper;
    @Autowired
    private NewBeeMallOrderItemMapper newBeeMallOrderItemMapper;
    @Autowired
    private NewBeeMallShoppingCartItemMapper newBeeMallShoppingCartItemMapper;
    @Autowired
    private NewBeeMallGoodsMapper newBeeMallGoodsMapper;

    @Resource
    private MallUserMapper mallUserMapper;

    @Override
    public String createOrder(NewBeeMallUserVO user, CreateOrderRequest createOrderRequest) {

        List<NewBeeMallShoppingCartItem> myShoppingCartItems;
        if (createOrderRequest.getFromType() == 1) {
            myShoppingCartItems = newBeeMallShoppingCartItemMapper.getCartByIds(createOrderRequest.getCartItemIdList());
        } else {
            List<NewBeeMallShoppingCartItemVO> list = createOrderRequest.getGoodsInfo();
            myShoppingCartItems = BeanUtil.copyList(list, NewBeeMallShoppingCartItem.class);
        }
        List<Long> goodsIds = myShoppingCartItems.stream().map(NewBeeMallShoppingCartItem::getGoodsId).collect(Collectors.toList());

        List<NewBeeMallGoods> newBeeMallGoods = newBeeMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        Map<Long, NewBeeMallGoods> goodsMap = new HashMap<>();
        for (NewBeeMallGoods goods : newBeeMallGoods) {
            goodsMap.put(goods.getGoodsId(), goods);
        }
        if (!CollectionUtils.isEmpty(newBeeMallGoods)) {
            //生成订单号
            String orderNo = NumberUtil.genOrderNo();
            int priceTotal = 0;
            //保存订单
            NewBeeMallOrder newBeeMallOrder = new NewBeeMallOrder();
            newBeeMallOrder.setOrderNo(orderNo);
            newBeeMallOrder.setUserId(user.getUserId());
            newBeeMallOrder.setUserAddress(createOrderRequest.getAddress());
            newBeeMallOrder.setUserName(createOrderRequest.getConsigneeName());
            newBeeMallOrder.setUserPhone(createOrderRequest.getPhone());
            newBeeMallOrder.setUserAddress(createOrderRequest.getAddress());
            //总价
            for (NewBeeMallShoppingCartItem newBeeMallShoppingCartItem : myShoppingCartItems) {
                Integer price = goodsMap.get(newBeeMallShoppingCartItem.getGoodsId()) == null ? 1 : goodsMap.get(newBeeMallShoppingCartItem.getGoodsId()).getSellingPrice();
                priceTotal += newBeeMallShoppingCartItem.getGoodsCount() * price;
            }
            if (priceTotal < 1) {
                NewBeeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
            }
            newBeeMallOrder.setTotalPrice(priceTotal);
            //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
            String extraInfo = "";
            newBeeMallOrder.setExtraInfo(extraInfo);
            // 保存渠道
            newBeeMallOrder.setChannelId(user.getChannelId() == null ? 0 : user.getChannelId());
            //生成订单项并保存订单项纪录
            newBeeMallOrderMapper.insertSelective(newBeeMallOrder);
            //生成所有的订单项快照，并保存至数据库
            List<NewBeeMallOrderItem> newBeeMallOrderItems = new ArrayList<>();
            Long orderId = newBeeMallOrder.getOrderId();
            for (NewBeeMallShoppingCartItem newBeeMallShoppingCartItem : myShoppingCartItems) {
                NewBeeMallOrderItem newBeeMallOrderItem = new NewBeeMallOrderItem();
                BeanUtil.copyProperties(newBeeMallShoppingCartItem, newBeeMallOrderItem);
                Integer price = goodsMap.get(newBeeMallShoppingCartItem.getGoodsId()) == null ? 1 : goodsMap.get(newBeeMallShoppingCartItem.getGoodsId()).getSellingPrice();
                String goodName = goodsMap.get(newBeeMallShoppingCartItem.getGoodsId()) == null ? "" : goodsMap.get(newBeeMallShoppingCartItem.getGoodsId()).getGoodsName();
                newBeeMallOrderItem.setSellingPrice(price);
                newBeeMallOrderItem.setOrderId(orderId);
                newBeeMallOrderItem.setGoodsName(goodName);
                newBeeMallOrderItem.setGoodsCoverImg(goodsMap.get(newBeeMallShoppingCartItem.getGoodsId()) == null ? "" : goodsMap.get(newBeeMallShoppingCartItem.getGoodsId()).getGoodsCoverImg());
                newBeeMallOrderItems.add(newBeeMallOrderItem);
            }
            //保存至数据库
            newBeeMallOrderItemMapper.insertBatch(newBeeMallOrderItems);
            // 删除购物车
            if (createOrderRequest.getFromType() == 1) {
                newBeeMallShoppingCartItemMapper.deleteBatch(createOrderRequest.getCartItemIdList());
            }
            //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
            return orderNo;

        }
        throw new NewBeeMallException(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
    }

    @Override
    public NewBeeMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        NewBeeMallOrder newBeeMallOrder = newBeeMallOrderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            List<NewBeeMallOrderItem> orderItems = newBeeMallOrderItemMapper.selectByOrderId(newBeeMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS = new ArrayList<>();
                for (NewBeeMallOrderItem newBeeMallOrderItem : orderItems) {
                    NewBeeMallOrderItemVO newBeeMallOrderItemVO = new NewBeeMallOrderItemVO();
                    newBeeMallOrderItemVO.setGoodsCount(newBeeMallOrderItem.getGoodsCount());
                    newBeeMallOrderItemVO.setGoodsCoverImg(newBeeMallOrderItem.getGoodsCoverImg());
                    newBeeMallOrderItemVO.setGoodsId(newBeeMallOrderItem.getGoodsId());
                    newBeeMallOrderItemVO.setGoodsName(newBeeMallOrderItem.getGoodsName());
                    BigDecimal result = new BigDecimal(newBeeMallOrderItem.getSellingPrice()).divide(new BigDecimal(100));
                    newBeeMallOrderItemVO.setSellingPrice(result.toString());
                    newBeeMallOrderItemVOS.add(newBeeMallOrderItemVO);
                }
                NewBeeMallOrderDetailVO newBeeMallOrderDetailVO = new NewBeeMallOrderDetailVO();
                BeanUtil.copyProperties(newBeeMallOrder, newBeeMallOrderDetailVO);
                newBeeMallOrderDetailVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(newBeeMallOrderDetailVO.getOrderStatus()).getName());
                newBeeMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(newBeeMallOrderDetailVO.getPayType()).getName());
                newBeeMallOrderDetailVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
                newBeeMallOrderDetailVO.setCreateTime(simpleDateFormat.format(newBeeMallOrder.getCreateTime()));
                if (newBeeMallOrder.getPayTime() != null && newBeeMallOrder.getOrderStatus() >= 1 && newBeeMallOrder.getOrderStatus() <= 4) {
                    newBeeMallOrderDetailVO.setPayTime(simpleDateFormat.format(newBeeMallOrder.getPayTime()));
                }
                BigDecimal result = new BigDecimal(newBeeMallOrder.getTotalPrice()).divide(new BigDecimal(100));
                newBeeMallOrderDetailVO.setTotalPrice(result.toString());
                return newBeeMallOrderDetailVO;
            }
        }
        return null;
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        if (pageUtil == null || pageUtil.get("userId") == null) {
            throw new NewBeeMallException(ServiceResultEnum.USER_ERROR.getResult());
        }
        int total = newBeeMallOrderMapper.getTotalNewBeeMallOrders(pageUtil);
        List<NewBeeMallOrder> newBeeMallOrders = newBeeMallOrderMapper.findNewBeeMallOrderList(pageUtil);
        List<NewBeeMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
//            orderListVOS = BeanUtil.copyList(newBeeMallOrders, NewBeeMallOrderListVO.class);
            for (NewBeeMallOrder newBeeMallOrder : newBeeMallOrders) {
                NewBeeMallOrderListVO newBeeMallOrderListVO = new NewBeeMallOrderListVO();
                BeanUtil.copyProperties(newBeeMallOrder, newBeeMallOrderListVO);
                orderListVOS.add(newBeeMallOrderListVO);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
                newBeeMallOrderListVO.setCreateTime(simpleDateFormat.format(newBeeMallOrder.getCreateTime()));
                if (newBeeMallOrder.getPayTime() != null && newBeeMallOrder.getOrderStatus() >= 1 && newBeeMallOrder.getOrderStatus() <= 4) {
                    newBeeMallOrderListVO.setPayTime(simpleDateFormat.format(newBeeMallOrder.getPayTime()));
                }
                BigDecimal result = new BigDecimal(newBeeMallOrder.getTotalPrice()).divide(new BigDecimal(100));
                newBeeMallOrderListVO.setTotalPrice(result.toString());
            }
            //设置订单状态中文显示值
            for (NewBeeMallOrderListVO newBeeMallOrderListVO : orderListVOS) {
                newBeeMallOrderListVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(newBeeMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = newBeeMallOrders.stream().map(NewBeeMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<NewBeeMallOrderItem> orderItems = newBeeMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<NewBeeMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(NewBeeMallOrderItem::getOrderId));
                for (NewBeeMallOrderListVO newBeeMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(newBeeMallOrderListVO.getOrderId())) {
                        List<NewBeeMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(newBeeMallOrderListVO.getOrderId());
                        //将NewBeeMallOrderItem对象列表转换成NewBeeMallOrderItemVO对象列表
                        List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS = new ArrayList<>();
                        for (NewBeeMallOrderItem newBeeMallOrderItem : orderItemListTemp) {
                            NewBeeMallOrderItemVO newBeeMallOrderItemVO = new NewBeeMallOrderItemVO();
                            newBeeMallOrderItemVO.setGoodsCount(newBeeMallOrderItem.getGoodsCount());
                            newBeeMallOrderItemVO.setGoodsCoverImg(newBeeMallOrderItem.getGoodsCoverImg());
                            newBeeMallOrderItemVO.setGoodsId(newBeeMallOrderItem.getGoodsId());
                            newBeeMallOrderItemVO.setGoodsName(newBeeMallOrderItem.getGoodsName());
                            BigDecimal result = new BigDecimal(newBeeMallOrderItem.getSellingPrice()).divide(new BigDecimal(100));
                            newBeeMallOrderItemVO.setSellingPrice(result.toString());
                            newBeeMallOrderItemVOS.add(newBeeMallOrderItemVO);
                        }
                        newBeeMallOrderListVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        NewBeeMallOrder newBeeMallOrder = newBeeMallOrderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            // 验证是否是当前userId下的订单，否则报错
            if (!userId.equals(newBeeMallOrder.getUserId())) {
                throw new NewBeeMallException(ServiceResultEnum.ORDER_PERMISSIONS_ERROR.getResult());
            }
            // 订单状态判断, 待支付与已支付 可取消
            if (!(newBeeMallOrder.getOrderStatus() == NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()
                    || newBeeMallOrder.getOrderStatus() == NewBeeMallOrderStatusEnum.OREDER_PAID.getOrderStatus())) {
                throw new NewBeeMallException(ServiceResultEnum.ORDER_CANCEL_STATUS_ERROR.getResult());
            }
            if (newBeeMallOrderMapper.closeOrder(Collections.singletonList(newBeeMallOrder.getOrderId()), NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public boolean completeOrderPayment(String orderNo, PayStatusEnum payStatusEnum) {
        NewBeeMallOrder newBeeMallOrder = newBeeMallOrderMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null && newBeeMallOrder.getPayStatus() == PayStatusEnum.PAY_ING.getPayStatus()) {
            NewBeeMallOrder newBeeMallOrder1 = new NewBeeMallOrder();
            newBeeMallOrder1.setOrderId(newBeeMallOrder.getOrderId());
            newBeeMallOrder1.setOrderStatus((byte) NewBeeMallOrderStatusEnum.OREDER_PAID.getOrderStatus());
            newBeeMallOrder1.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            newBeeMallOrder1.setPayTime(new Date());
            newBeeMallOrder1.setUpdateTime(new Date());
            newBeeMallOrderMapper.updateByPrimaryKeySelective(newBeeMallOrder1);
            return true;
        }
        return false;
    }

    @Override
    public List<NewBeeMallOrderListVO> getOrdersByExport(PageQueryUtil pageUtil) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<NewBeeMallOrder> newBeeMallOrders = newBeeMallOrderMapper.findNewBeeMallOrderListByExport(pageUtil);
        List<NewBeeMallOrderListVO> orderListVOS = new ArrayList<>();
        List<Long> userIds = newBeeMallOrders.stream().map(NewBeeMallOrder::getUserId).distinct().collect(Collectors.toList());
        //数据转换 将实体类转成vo
        for (NewBeeMallOrder newBeeMallOrder : newBeeMallOrders) {
            NewBeeMallOrderListVO newBeeMallOrderListVO = new NewBeeMallOrderListVO();
            newBeeMallOrderListVO.setOrderId(newBeeMallOrder.getOrderId());
            newBeeMallOrderListVO.setOrderNo(newBeeMallOrder.getOrderNo());
            newBeeMallOrderListVO.setUserAddress(newBeeMallOrder.getUserAddress());
            newBeeMallOrderListVO.setUserId(newBeeMallOrder.getUserId());
            newBeeMallOrderListVO.setOrderStatus(newBeeMallOrder.getOrderStatus());
            if (newBeeMallOrder.getPayTime() != null) {
                newBeeMallOrderListVO.setCreateTime(simpleDateFormat.format(newBeeMallOrder.getCreateTime()));
            }
            orderListVOS.add(newBeeMallOrderListVO);
        }
        // 查询用户信息
        List<MallUser> userList = mallUserMapper.lisMallUserByIds(userIds);
        Map<Long, MallUser> userMap = userList.stream().collect(Collectors.toMap(MallUser::getUserId, it -> it));

        //设置订单状态中文显示值 && 设置用户信息
        for (NewBeeMallOrderListVO newBeeMallOrderListVO : orderListVOS) {
            newBeeMallOrderListVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(newBeeMallOrderListVO.getOrderStatus()).getName());
            if (userMap.containsKey(newBeeMallOrderListVO.getUserId())) {
                MallUser mallUser = userMap.get(newBeeMallOrderListVO.getUserId());
                newBeeMallOrderListVO.setPhone(mallUser.getLoginName());
                newBeeMallOrderListVO.setRecipient(mallUser.getNickName());
            }
        }
        List<Long> orderIds = newBeeMallOrders.stream().map(NewBeeMallOrder::getOrderId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(orderIds)) {
            List<NewBeeMallOrderItem> orderItems = newBeeMallOrderItemMapper.selectByOrderIds(orderIds);
            Map<Long, List<NewBeeMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(NewBeeMallOrderItem::getOrderId));
            for (NewBeeMallOrderListVO newBeeMallOrderListVO : orderListVOS) {
                //封装每个订单列表对象的订单项数据
                if (itemByOrderIdMap.containsKey(newBeeMallOrderListVO.getOrderId())) {
                    List<NewBeeMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(newBeeMallOrderListVO.getOrderId());
                    //将NewBeeMallOrderItem对象列表转换成NewBeeMallOrderItemVO对象列表
                    List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, NewBeeMallOrderItemVO.class);
                    List<Long> goodsIdList = newBeeMallOrderItemVOS.stream().map(NewBeeMallOrderItemVO::getGoodsId).distinct().collect(Collectors.toList());
                    List<NewBeeMallGoods> newBeeMallGoodsList = newBeeMallGoodsMapper.selectOriginalPriByIdList(goodsIdList);
                    Map<Long, Integer> priceMap = newBeeMallGoodsList.stream().collect(Collectors.toMap(NewBeeMallGoods::getGoodsId, it -> it.getOriginalPrice()));
                    for (NewBeeMallOrderItemVO newBeeMallOrderItemVO : newBeeMallOrderItemVOS) {
                        if (priceMap.containsKey(newBeeMallOrderItemVO.getGoodsId())) {
                            Integer originalPrice = priceMap.get(newBeeMallOrderItemVO.getGoodsId());
                            newBeeMallOrderItemVO.setOriginalPrice(new BigDecimal(originalPrice).divide(new BigDecimal(100)).toString());
                        }
                    }
                    newBeeMallOrderListVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
                }
            }
        }

        return orderListVOS;
    }
}
