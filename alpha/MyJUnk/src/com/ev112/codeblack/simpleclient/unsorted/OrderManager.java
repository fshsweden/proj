package com.ev112.codeblack.simpleclient.unsorted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ev112.codeblack.common.exchange.TimeInForce;
import com.ev112.codeblack.common.ordercontroller.OrderException;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.OrderStatus;
import com.ib.client.OrderType;
import com.ib.client.Types.Action;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.ILiveOrderHandler;
import com.ib.controller.ApiController.IOrderHandler;

public class OrderManager {

	/**
	 * P R O P E R T I E S
	 */
	private final ApiController		apiController;
	private Map<Integer, OrderData>	orders	= new HashMap<Integer, OrderData>();

	/**
	 * M E T H O D S
	 */
	public OrderManager(ApiController apiController) {
		this.apiController = apiController;
	}

	public void start() {
		requestLiveOrders();
	}

	public void placeOrderIB(Contract contract, // NOTE must include exchange!
		Action action,
		int qty,
		OrderType typ,
		TimeInForce tif,
		double price,
		String acct,
		String ref) {
		final Order neworder = new Order();

		neworder.action(action);
		neworder.totalQuantity(qty);

		switch (typ) {
			case MKT:
				neworder.orderType(OrderType.MKT);
			break;

			default:
			case LMT:
				neworder.orderType(OrderType.LMT);
			break;
		}

		switch (tif) {
			case IOC:
				neworder.tif(TimeInForce.IOC);
			break;
			default:
			case DAY:
				neworder.tif(TimeInForce.DAY);
			break;
		}

		neworder.lmtPrice(price);
		neworder.account(acct); // CONFIGURED ACOCUNT!
		neworder.orderRef(ref);

		// placeOrderIB(contract, neworder);
	}

	/**
	 * 
	 * @param ownorder
	 * @param instr
	 * @return
	 * @throws OrderException
	 */
	private OrderData placeOrderIB(final Contract c, final Order o) throws OrderException {

		final OrderData od = new OrderData(c, o, null);

		System.out.println("===== PLACING ORDER IN ?? ");

		/**
		 * placeOrModifyOrder will set a unique order id
		 */
		apiController.placeOrModifyOrder(c, o, new IOrderHandler() {

			@Override
			public void orderState(OrderState orderState) {

				System.out.println("===== PLACEORDER: ORDERSTATUS IS " + orderState.status().name());

				switch (orderState.status()) {
				// PendingSubmit - indicates that you have transmitted the order, but have not yet received
				// confirmation that it has been accepted by the order destination.
				// NOTE: This order status is not sent by TWS and should be explicitly set by
				// the API developer when an order is submitted.
					case PendingSubmit:
					break;
					// PendingCancel - indicates that you have sent a request to cancel the order but have not yet
					// received cancel confirmation from the order destination. At this point,
					// your order is not confirmed canceled. You may still receive an execution
					// while your cancellation request is pending. NOTE: This order status is not
					// sent by TWS and should be explicitly set by the API developer when an order
					// is canceled.
					case PendingCancel:
					break;
					// PreSubmitted - indicates that a simulated order type has been accepted by the IB system and
					// that this order has yet to be elected. The order is held in the IB system
					// until the election criteria are met. At that time the order is transmitted
					// to the order destination as specified .
					case PreSubmitted:
					break;

					// Submitted - indicates that your order has been accepted at the order destination and is working.
					case Submitted:
						synchronized (orders) {
							orders.put(o.orderId(), od);
						}
						apiController.removeOrderHandler(this);
					/*
					 * We dont need this handler anymore!
					 * the rest of order handling is done by the Live Order handler
					 */
					break;
					// Cancelled - indicates that the balance of your order has been confirmed canceled by the IB
					// system. This could occur unexpectedly when IB or the destination has rejected
					// your order.
					case Cancelled:
						synchronized (orders) {
							orders.put(o.orderId(), od);
						}
						apiController.removeOrderHandler(this);
					break;
					// Filled - indicates that the order has been completely filled.
					case Filled:
						synchronized (orders) {
							orders.put(o.orderId(), od);
						}
						apiController.removeOrderHandler(this);
					break;
					// Inactive - indicates that the order has been accepted by the system (simulated orders)
					// or an exchange (native orders) but that currently the order is inactive
					// due to system, exchange or other issues.
					case Inactive:
						synchronized (orders) {
							orders.put(o.orderId(), od);
						}
						apiController.removeOrderHandler(this);
					break;

					case ApiCancelled:
						/* Don't know what this is!! */
						synchronized (orders) {
							orders.put(o.orderId(), od);
						}
						apiController.removeOrderHandler(this);
					break;

					case ApiPending:
					break;

					case Unknown:
					break;

					default:
					break;
				}
			}

			@Override
			public void handle(int errorCode, String errorMsg) {
				System.out.println("===== placeOrModifyOrder.handle() : Error is " + errorCode + " Msg:" + errorMsg);
				System.out.println("===== reporting ERROR back... ");

				// ownorder.setSubState("REJECTED");
				// mCallback.ownOrderUpdate(new OrderUpdateEvent(ownorder, EventType.ERROR, System.currentTimeMillis()));
			}
		});

		return null;
	}

	private void listOrders() {
		System.out.println("----------- ORDERLIST -------------------------------------------------");
		for (OrderData od : orders.values()) {

			if (od.getOrderState().status() != OrderStatus.Cancelled && od.getOrderState().status() != OrderStatus.Filled) {
				System.out.println("Symbol:" + od.getContract().symbol() + " Action:" + od.getOrder().action() + " LmtPrice:" + od.getOrder().lmtPrice() + " AuxPrice:"
						+ od.getOrder().auxPrice() + " Qty:" + od.getOrder().totalQuantity() + " PermId:" + od.getOrder().permId() + " CliOrdId:" + od.getOrder().orderId());
			}
		}
		System.out.println("----------- END -------------------------------------------------------");
	}

	private List<OrderData> listExecutions() {
		List<OrderData> o = new ArrayList<OrderData>();
		for (OrderData od : orders.values()) {
			if (od.getOrderState().status() != OrderStatus.Filled) {
				o.add(od);
			}
		}

		return o;
	}

	private void requestLiveOrders() {

		apiController.takeFutureTwsOrders(new ILiveOrderHandler() {

			@Override
			public void orderStatus(
				int orderId,
				OrderStatus status,
				int filled,
				int remaining,
				double avgFillPrice,
				long permId,
				int parentId,
				double lastFillPrice,
				int clientId,
				String whyHeld) 
			{
				// System.out.println(
				// "LIVEORDERS-ORDERSTATUS: norderid:" + orderId +
				// "    status:" + status +
				// "    filled:" + filled +
				// "    remaining:" + remaining);
				//
				OrderData od = orders.get(orderId);
				if (od == null) {
					System.out.println("orderStatus: order " + orderId + " not found!");

					// new OrderData(NewContract contract,
					// NewOrder order,
					// NewOrderState orderState);

					OrderState os = new OrderState("", // Status
							"", // String initMargin,
							"", // String maintMargin,
							"", // String equityWithLoan,
							0.0, // double commission,
							0.0, // double minCommission,
							0.0, // double maxCommission,
							"", // String commissionCurrency,
							"" // String warningText
					);

					NewOrderState new_os = new NewOrderState(os);

				} 
				else {
//					int orderId,
//					OrderStatus status,
//					int filled,
//					int remaining,
//					double avgFillPrice,
//					long permId,
//					int parentId,
//					double lastFillPrice,
//					int clientId,
//					String whyHeld)
					
					// update status
					orders.get(orderId).getOrderState().status(status);
				}
				//
				// // update Order Data Fields
				// System.out.println("OrderStatus Storing order data for id " + orderId);

				listOrders();

			}

			@Override
			public void openOrderEnd() {
				System.out.println("--- LIVEORDERS - END OF ORDERS ---");
				listOrders();
			}

			@Override
			public void openOrder(NewContract contract, NewOrder order, NewOrderState orderState) {
				// System.out.println("LIVEORDERS-OPENORDER " + contract.symbol() +
				// "    Acct:" + order.account() +
				// "    Id:" + order.toString() +
				// "    TotalQty:" + order.totalQuantity() +
				// "    state:" + orderState.status()
				// );

				OrderData od = new OrderData(contract, order, orderState);

				if (orders.get(order.orderId()) != null) {
					// System.out.println("STRANGE: Order " + order.orderId() + " already existed??");
					// UPDATE
					
					orders.put(order.orderId(), od);
				}

				System.out.println("OpenOrder Storing order id " + order.orderId());
				orders.put(order.orderId(), od);
			}

			@Override
			public void handle(int orderId, int errorCode, String errorMsg) {
				System.out.println("LIVEORDERS: HANDLE: orderId:" + orderId + " error:" + errorCode + " msg:" + errorMsg);
			}
		});
	}
}
