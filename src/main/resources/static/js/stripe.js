const stripe = Stripe('pk_test_51OElCgDShBlLoPnv24xCm3p3lU17jrVhlViVY4bxuHG8E01z6oIEUwxP8D73bfjTN9iJK61Ad9tPY523Kn4hZnZ1000Ncl5fEn');
 const paymentButton = document.querySelector('#paymentButton');
 
 paymentButton.addEventListener('click', () => {
   stripe.redirectToCheckout({
     sessionId: sessionId
   })
 });